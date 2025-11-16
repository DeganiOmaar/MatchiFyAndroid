package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("_id") val id: String? = null,
    val fullName: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val profileImage: String? = null,
    val bannerImage: String? = null,
    val location: String? = null,
    val talent: String? = null,
    val description: String? = null,
    val skills: List<String>? = null, // Talent skills (max 10)
    val portfolioLink: String? = null, // Talent portfolio URL
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val profileImageUrl: String?
        get() {
            // Return null if profileImage is null, empty, or blank
            val path = profileImage?.trim()
            if (path.isNullOrBlank()) return null
            
            // Normalize path: add leading slash if missing
            // Example: "uploads/profile/image.jpg" -> "/uploads/profile/image.jpg"
            val normalized = if (path.startsWith("/")) path else "/$path"
            val fullUrl = "http://10.0.2.2:3000$normalized"
            
            // Debug log (remove in production if needed)
            android.util.Log.d("UserModel", "Profile image URL: $fullUrl (from path: $path)")
            
            return fullUrl
        }
}