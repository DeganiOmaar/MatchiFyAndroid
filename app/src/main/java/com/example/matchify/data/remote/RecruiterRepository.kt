package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.profile.RecruiterProfileResponseDto
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

    suspend fun getRecruiterProfile(): RecruiterProfileResponseDto =
        withContext(Dispatchers.IO) {
            api.getRecruiterProfile()
        }


    suspend fun updateRecruiterProfile(
        fullName: String?,
        email: String?,
        phone: String?,
        location: String?,
        description: String?,
        imageFile: File?
    ): Unit =
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

    suspend fun saveUpdatedUser(user: UserModel) {
        prefs.saveUser(user)
    }
}

fun String.toMultipartString(): RequestBody =
    RequestBody.create("text/plain".toMediaTypeOrNull(), this)