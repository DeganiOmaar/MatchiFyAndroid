package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.profile.TalentProfileResponseDto
import com.example.matchify.domain.model.UserModel
import com.example.matchify.data.remote.toMultipartString
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class TalentRepository(
    private val api: TalentApi,
    private val prefs: AuthPreferences
) {

    suspend fun getTalentProfile(): TalentProfileResponseDto =
        withContext(Dispatchers.IO) {
            api.getTalentProfile()
        }

    suspend fun updateTalentProfile(
        fullName: String?,
        email: String?,
        phone: String?,
        location: String?,
        talent: List<String>?,
        description: String?,
        skills: List<String>?,
        imageFile: File?
    ): TalentProfileResponseDto =
        withContext(Dispatchers.IO) {

            val requestFullName = fullName?.toMultipartString()
            val requestEmail = email?.toMultipartString()
            val requestPhone = phone?.toMultipartString()
            val requestLocation = location?.toMultipartString()
            val requestDescription = description?.toMultipartString()

            val requestTalent = talent?.let {
                val gson = Gson()
                val jsonString = gson.toJson(it)
                RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
            }

            val requestSkills = skills?.let {
                val gson = Gson()
                val jsonString = gson.toJson(it)
                RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
            }

            val imagePart = imageFile?.let {
                val body = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                MultipartBody.Part.createFormData("profileImage", it.name, body)
            }

            api.updateTalentProfile(
                fullName = requestFullName,
                email = requestEmail,
                phone = requestPhone,
                location = requestLocation,
                talent = requestTalent,
                description = requestDescription,
                skills = requestSkills,
                profileImage = imagePart
            )
        }


    suspend fun saveUpdatedUser(user: UserModel) {
        prefs.saveUser(user)
    }
    
    /**
     * Upload CV (PDF/DOC/DOCX)
     * Même endpoint que iOS: POST /talent/upload-cv
     */
    suspend fun uploadCV(file: java.io.File): TalentProfileResponseDto = withContext(Dispatchers.IO) {
        // Déterminer le type MIME
        val mimeType = when (file.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "application/octet-stream"
        }
        
        val requestBody = RequestBody.create(
            mimeType.toMediaTypeOrNull(),
            file
        )
        
        val filePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestBody
        )
        
        api.uploadCV(filePart)
    }
}

