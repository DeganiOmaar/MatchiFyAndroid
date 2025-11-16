package com.example.matchify.ui.talent.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.data.remote.dto.toDomain
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditTalentProfileViewModel(
    private val repository: TalentRepository,
    private val context: Context,
    private val prefs: AuthPreferences
) : ViewModel() {

    val fullName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val location = MutableStateFlow("")
    val talent = MutableStateFlow("")
    val description = MutableStateFlow("")
    val skills = MutableStateFlow<List<String>>(emptyList())
    val portfolioLink = MutableStateFlow("")
    val skillInput = MutableStateFlow("")
    val selectedImageUri = MutableStateFlow<Uri?>(null)
    val currentProfileImageUrl = MutableStateFlow<String?>(null)

    val saving = MutableStateFlow(false)
    val saved = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    /** Called when navigating from profile */
    fun loadInitial(user: UserModel) {
        fullName.value = user.fullName
        email.value = user.email
        phone.value = user.phone ?: ""
        location.value = user.location ?: ""
        talent.value = user.talent ?: ""
        description.value = user.description ?: ""
        skills.value = user.skills ?: emptyList()
        portfolioLink.value = user.portfolioLink ?: ""
        currentProfileImageUrl.value = user.profileImageUrl
    }

    /** Set selected image URI */
    fun setSelectedImageUri(uri: Uri) {
        selectedImageUri.value = uri
    }

    /** Add skill */
    fun addSkill() {
        val trimmed = skillInput.value.trim()
        if (trimmed.isNotEmpty() && skills.value.size < 10 && !skills.value.contains(trimmed)) {
            skills.value = skills.value + trimmed
            skillInput.value = ""
        }
    }

    /** Remove skill */
    fun removeSkill(skill: String) {
        skills.value = skills.value.filter { it != skill }
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

        if (skills.value.size > 10) {
            error.value = "Maximum 10 compétences autorisées."
            return
        }
        
        saving.value = true

        viewModelScope.launch {
            try {
                // Convert URI to File if image is selected
                val imageFile = selectedImageUri.value?.let { uriToFile(it) }

                val response = repository.updateTalentProfile(
                    fullName = fullName.value,
                    email = email.value,
                    phone = phone.value.ifBlank { null },
                    location = location.value.ifBlank { null },
                    talent = talent.value.ifBlank { null },
                    description = description.value.ifBlank { null },
                    skills = if (skills.value.isNotEmpty()) skills.value else null,
                    portfolioLink = portfolioLink.value.ifBlank { null },
                    imageFile = imageFile
                )

                // Save updated user in DataStore
                val updatedUser = response.user.toDomain()
                repository.saveUpdatedUser(updatedUser)
                
                // Update current profile image URL
                currentProfileImageUrl.value = updatedUser.profileImageUrl

                saving.value = false
                saved.value = true

            } catch (e: Exception) {
                saving.value = false
                error.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
            }
        }
    }
}

