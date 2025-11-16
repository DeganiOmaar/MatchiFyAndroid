package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.RecruiterProfileResponseDto
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RecruiterRepository(
    private val api: RecruiterApi,
    private val prefs: AuthPreferences
) {

    // 🔹 GET recruiter profile
    suspend fun getRecruiterProfile(): RecruiterProfileResponseDto =
        withContext(Dispatchers.IO) {
            api.getRecruiterProfile()
        }

    // 🔹 UPDATE recruiter profile (multipart)
    suspend fun updateRecruiterProfile(
        fullName: String?,
        email: String?,
        phone: String?,
        location: String?,
        description: String?,
        imageFile: File?
    ): RecruiterProfileResponseDto =
        withContext(Dispatchers.IO) {

            val requestFullName = fullName?.toMultipartString()
            val requestEmail = email?.toMultipartString()
            val requestPhone = phone?.toMultipartString()
            val requestLocation = location?.toMultipartString()
            val requestDescription = description?.toMultipartString()

            val imagePart = imageFile?.let {
                val body = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                MultipartBody.Part.createFormData("profileImage", it.name, body)
            }

            api.updateRecruiterProfile(
                fullName = requestFullName,
                email = requestEmail,
                phone = requestPhone,
                location = requestLocation,
                description = requestDescription,
                profileImage = imagePart
            )
        }

    // 🔹 Save updated user into DataStore
    suspend fun saveUpdatedUser(user: UserModel) {
        prefs.saveUser(user)
    }
}

// 🔹 Helper for multipart text fields
fun String.toMultipartString(): RequestBody =
    RequestBody.create("text/plain".toMediaTypeOrNull(), this)