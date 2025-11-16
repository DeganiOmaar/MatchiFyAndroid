package com.example.matchify.ui.recruiter.edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.RecruiterRepository
import com.example.matchify.data.remote.dto.toDomain
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditRecruiterProfileViewModel(
    private val repository: RecruiterRepository,
    private val context: Context,
    private val prefs: AuthPreferences
) : ViewModel() {

    val fullName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val location = MutableStateFlow("")
    val description = MutableStateFlow("")
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
        description.value = user.description ?: ""
        currentProfileImageUrl.value = user.profileImageUrl
    }

    /** Set selected image URI */
    fun setSelectedImageUri(uri: Uri) {
        selectedImageUri.value = uri
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

                val response = repository.updateRecruiterProfile(
                    fullName = fullName.value,
                    email = email.value,
                    phone = phone.value.ifBlank { null },
                    location = location.value.ifBlank { null },
                    description = description.value.ifBlank { null },
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
