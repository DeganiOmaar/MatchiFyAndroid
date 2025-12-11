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
import kotlinx.coroutines.flow.StateFlow
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
    val skills = MutableStateFlow<List<String>>(emptyList())
    val skillInput = MutableStateFlow("")
    val selectedImageUri = MutableStateFlow<Uri?>(null)
    val currentProfileImageUrl = MutableStateFlow<String?>(null)

    val saving = MutableStateFlow(false)
    val saved = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)
    
    // Store skill IDs separately for submission
    private var skillIds = listOf<String>()

    /** Called when navigating from profile */
    fun loadInitial(user: UserModel) {
        fullName.value = user.fullName
        email.value = user.email
        phone.value = user.phone ?: ""
        location.value = user.location ?: ""
        talents.value = user.talent ?: emptyList()
        description.value = user.description ?: ""
        currentProfileImageUrl.value = user.profileImageUrl
        
        // Load skill names from IDs
        val userSkillIds = user.skills ?: emptyList()
        skillIds = userSkillIds
        loadSkillNames(userSkillIds)
    }
    
    /** Load skill names from IDs */
    private fun loadSkillNames(skillIdsList: List<String>) {
        if (skillIdsList.isEmpty()) {
            skills.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val skillsList = skillRepository.getSkillsByIds(skillIdsList)
                skills.value = skillsList.mapNotNull { it.name }
                Log.d("EditTalentProfileViewModel", "Loaded skill names: ${skills.value}")
            } catch (e: Exception) {
                Log.e("EditTalentProfileViewModel", "Failed to load skill names: ${e.message}", e)
                // Fallback: use IDs if loading names fails
                skills.value = skillIdsList
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

    /** Add skill - search for skill by name and add it */
    fun addSkill() {
        val trimmed = skillInput.value.trim()
        if (trimmed.isEmpty() || skills.value.contains(trimmed)) {
            return
        }
        
        viewModelScope.launch {
            try {
                // Search for the skill
                val searchResults = skillRepository.searchSkills(trimmed)
                
                if (searchResults.isNotEmpty()) {
                    // Use the first matching skill
                    val skill = searchResults.first()
                    val skillId = skill.uniqueId
                    
                    // Check if we already have this skill (by ID)
                    if (!skillIds.contains(skillId)) {
                        // Add skill name to display list
                        skills.value = skills.value + skill.name
                        // Add skill ID to submission list
                        skillIds = skillIds + skillId
                        skillInput.value = ""
                        Log.d("EditTalentProfileViewModel", "Added skill: ${skill.name} ($skillId)")
                    } else {
                        // Skill already added
                        skillInput.value = ""
                    }
                } else {
                    // No matching skill found - you could show an error here
                    Log.w("EditTalentProfileViewModel", "No skill found matching: $trimmed")
                    error.value = "Skill not found. Please select from available skills."
                    // Clear error after 3 seconds
                    kotlinx.coroutines.delay(3000)
                    error.value = null
                }
            } catch (e: Exception) {
                Log.e("EditTalentProfileViewModel", "Error searching for skill: ${e.message}", e)
                error.value = "Error searching for skill"
                kotlinx.coroutines.delay(3000)
                error.value = null
            }
        }
    }

    /** Remove skill */
    fun removeSkill(skillName: String) {
        // Find the index of the skill name
        val index = skills.value.indexOf(skillName)
        if (index >= 0 && index < skillIds.size) {
            // Remove from both lists
            skills.value = skills.value.filterIndexed { i, _ -> i != index }
            skillIds = skillIds.filterIndexed { i, _ -> i != index }
            Log.d("EditTalentProfileViewModel", "Removed skill: $skillName")
        }
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
            error.value = "Le nom et l'email sont requis."
            return
        }
        
        saving.value = true

        viewModelScope.launch {
            try {
                // Convert URI to File if image is selected
                val imageFile = selectedImageUri.value?.let { uriToFile(it) }

                // 1) Appel d'update : succès si la requête passe sans exception.
                // IMPORTANT: Send skill IDs, not skill names
                repository.updateTalentProfile(
                    fullName = fullName.value,
                    email = email.value,
                    phone = phone.value.ifBlank { null },
                    location = location.value.ifBlank { null },
                    talent = if (talents.value.isNotEmpty()) talents.value else null,
                    description = description.value.ifBlank { null },
                    skills = if (skillIds.isNotEmpty()) skillIds else null, // Send IDs, not names
                    imageFile = imageFile
                )

                // 2) Tentative de rafraîchissement du profil complet, sans
                // impacter le succès si ça échoue.
                kotlin.runCatching {
                    val refreshedUserDto = repository.getTalentProfile().user
                    if (refreshedUserDto != null) {
                        val refreshedUser = refreshedUserDto.toDomain()
                        repository.saveUpdatedUser(refreshedUser)
                        currentProfileImageUrl.value = refreshedUser.profileImageUrl
                    }
                }

                saving.value = false
                saved.value = true

            } catch (e: Exception) {
                saving.value = false
                error.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
            }
        }
    }
}

