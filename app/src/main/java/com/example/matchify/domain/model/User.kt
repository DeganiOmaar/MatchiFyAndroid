package com.example.matchify.domain.model

data class UserModel(
    val id: String? = null,
    val fullName: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val profileImage: String? = null,
    val bannerImage: String? = null,
    val location: String? = null,
    val talent: String? = null,
    val description: String? = null, // NEW
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val profileImageUrl: String?
        get() {
            val path = profileImage ?: return null
            val normalized = if (path.startsWith("/")) path else "/$path"
            return "http://10.0.2.2:3000$normalized"
        }
}