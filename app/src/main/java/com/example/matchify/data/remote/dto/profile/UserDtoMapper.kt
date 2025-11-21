package com.example.matchify.data.remote.dto.profile

import com.example.matchify.domain.model.UserModel

fun UserDto.toDomain(): UserModel {
    return UserModel(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
        phone = phone,
        profileImage = profileImage,
        bannerImage = bannerImage,
        location = location,
        talent = talent,
        description = description,
        skills = skills,
        portfolioLink = portfolioLink,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

