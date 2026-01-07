package com.example.matchify.ui.talent.edit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.data.remote.dto.profile.toDomain
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.matchify.domain.model.Skill
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditTalentProfileViewModel(
    private val repository: TalentRepository,
    private val skillRepository: SkillRepository,
    private val context: Context,
    private val prefs: AuthPreferences
) : ViewModel() {



    val fullName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val location = MutableStateFlow("")
    val talents = MutableStateFlow<List<String>>(emptyList())
    val talentInput = MutableStateFlow("")
    val description = MutableStateFlow("")
    val skills = MutableStateFlow<List<Skill>>(emptyList<Skill>())
    // skillInput not needed anymore for View but keeping if other dependencies exist (will be unused)
    val skillInput = MutableStateFlow("") 
    val selectedImageUri = MutableStateFlow<Uri?>(null)
    val currentProfileImageUrl = MutableStateFlow<String?>(null)

    val saving = MutableStateFlow(false)
    val saved = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
    
    // Store skill IDs separately for submission - logic can be derived from skills now
    // private var skillIds = listOf<String>() // Removed, we use skills.value

    /** Called when navigating from profile */
    fun loadInitial(user: UserModel) {
        fullName.value = user.fullName
        email.value = user.email
        phone.value = user.phone ?: ""
        location.value = user.location ?: ""
        talents.value = user.talent ?: emptyList()
        description.value = user.description ?: ""
        currentProfileImageUrl.value = user.profileImageUrl
        
        // Load skills
        val userSkillIds = user.skills ?: emptyList()
        loadSkills(userSkillIds)
    }
    
    /** Load skills from IDs */
    private fun loadSkills(skillIdsList: List<String>) {
        if (skillIdsList.isEmpty()) {
            skills.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val skillsList = skillRepository.getSkillsByIds(skillIdsList)
                
                // Filter out skills where name looks like a MongoDB ID (24-character hex string)
                val validSkills = skillsList.filter { skill ->
                    val name = skill.name
                    // MongoDB IDs are 24-character hex strings
                    val isMongoId = name.length == 24 && name.all { 
                        it.isDigit() || it in 'a'..'f' || it in 'A'..'F' 
                    }
                    !isMongoId
                }
                
                skills.value = validSkills
                
                if (validSkills.size < skillsList.size) {
                    Log.w("EditTalentProfileViewModel", "⚠️ Filtered out ${skillsList.size - validSkills.size} skills with invalid names (IDs)")
                }
                
                Log.d("EditTalentProfileViewModel", "Loaded skills: ${skills.value.map { it.name }}")
            } catch (e: Exception) {
                Log.e("EditTalentProfileViewModel", "Failed to load skills: ${e.message}", e)
                skills.value = emptyList()
            }
        }
    }

    /** Set selected image URI */
    fun setSelectedImageUri(uri: Uri) {
        selectedImageUri.value = uri
    }

    /** Add talent */
    fun addTalent() {
        val trimmed = talentInput.value.trim()
        if (trimmed.isNotEmpty() && !talents.value.contains(trimmed)) {
            talents.value = talents.value + trimmed
            talentInput.value = ""
        }
    }

    /** Remove talent */
    fun removeTalent(talent: String) {
        talents.value = talents.value.filter { it != talent }
    }

    // Old addSkill/removeSkill methods removed/replaced by updateSelectedSkills
    
    fun updateSelectedSkills(newSkills: List<Skill>) {
        skills.value = newSkills
    }

    /** Convert URI to File */
    private suspend fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val tempFile = File(context.cacheDir, "temp_profile_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(tempFile)
                stream.copyTo(outputStream)
                outputStream.close()
                tempFile
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Submit update */
    fun submit() {
        error.value = null
        
        // Validation
        if (fullName.value.isBlank() || email.value.isBlank()) {
            error.value = "Name and email are required."
            return
        }
        
        saving.value = true

        viewModelScope.launch {
            try {
                // Convert URI to File if image is selected
                val imageFile = selectedImageUri.value?.let { uriToFile(it) }

                val currentSkills = skills.value
                val skillIdsToSend = currentSkills.mapNotNull { it.uniqueId ?: it.id }
                
                Log.d("EditTalentProfileViewModel", "=== SUBMITTING PROFILE ===")
                Log.d("EditTalentProfileViewModel", "Current skills in UI: ${currentSkills.map { "${it.name} (ID: ${it.uniqueId ?: it.id})" }}")
                Log.d("EditTalentProfileViewModel", "Skill IDs to send to backend: $skillIdsToSend")
                
                // IMPORTANT: Send skill IDs
                repository.updateTalentProfile(
                    fullName = fullName.value,
                    email = email.value,
                    phone = phone.value.ifBlank { null },
                    location = location.value.ifBlank { null },
                    talent = if (talents.value.isNotEmpty()) talents.value else null,
                    description = description.value.ifBlank { null },
                    skills = if (skillIdsToSend.isNotEmpty()) skillIdsToSend else null,
                    imageFile = imageFile
                )

                Log.d("EditTalentProfileViewModel", "✅ Profile update successful, refreshing...")

                // 2) Refresh profile
                kotlin.runCatching {
                    val refreshedUserDto = repository.getTalentProfile().user
                    if (refreshedUserDto != null) {
                        val refreshedUser = refreshedUserDto.toDomain()
                        Log.d("EditTalentProfileViewModel", "Refreshed user skills from backend: ${refreshedUser.skills}")
                        repository.saveUpdatedUser(refreshedUser)
                        currentProfileImageUrl.value = refreshedUser.profileImageUrl
                        Log.d("EditTalentProfileViewModel", "✅ Profile refreshed and saved to local storage")
                    } else {
                        Log.w("EditTalentProfileViewModel", "⚠️ Refreshed user DTO is null")
                    }
                }.onFailure { e ->
                    Log.e("EditTalentProfileViewModel", "❌ Failed to refresh profile: ${e.message}", e)
                }

                saving.value = false
                saved.value = true

            } catch (e: Exception) {
                Log.e("EditTalentProfileViewModel", "❌ Failed to update profile: ${e.message}", e)
                saving.value = false
                error.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
            }
        }
    }
}

