package com.example.matchify.data.remote.dto

import com.example.matchify.domain.model.UserModel

// ⭐ Convert RecruiterUserDto → UserModel
fun RecruiterUserDto.toDomain(): UserModel {
    return UserModel(
        id = this.id,
        fullName = this.fullName,
        email = this.email,
        role = this.role,
        phone = this.phone,
        profileImage = this.profileImage,
        bannerImage = null,
        location = this.location,
        talent = this.talent,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}