package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.TalentProfileResponseDto
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

    // 🔹 GET talent profile
    suspend fun getTalentProfile(): TalentProfileResponseDto =
        withContext(Dispatchers.IO) {
            api.getTalentProfile()
        }

    // 🔹 UPDATE talent profile (multipart)
    suspend fun updateTalentProfile(
        fullName: String?,
        email: String?,
        phone: String?,
        location: String?,
        talent: String?,
        description: String?,
        skills: List<String>?,
        portfolioLink: String?,
        imageFile: File?
    ): TalentProfileResponseDto =
        withContext(Dispatchers.IO) {

            val requestFullName = fullName?.toMultipartString()
            val requestEmail = email?.toMultipartString()
            val requestPhone = phone?.toMultipartString()
            val requestLocation = location?.toMultipartString()
            val requestTalent = talent?.toMultipartString()
            val requestDescription = description?.toMultipartString()
            val requestPortfolioLink = portfolioLink?.toMultipartString()

            // Convert skills list to JSON string
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
                portfolioLink = requestPortfolioLink,
                profileImage = imagePart
            )
        }

    // 🔹 Save updated user into DataStore
    suspend fun saveUpdatedUser(user: UserModel) {
        prefs.saveUser(user)
    }
}

