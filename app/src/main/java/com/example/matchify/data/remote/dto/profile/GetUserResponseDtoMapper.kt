package com.example.matchify.data.remote.dto.profile

import com.example.matchify.data.remote.dto.portfolio.toDomain
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.UserModel

fun GetUserResponseDto.toDomain(talentIdFallback: String? = null): Pair<UserModel, List<Project>> {
    val user = user.toDomain()
    val portfolio = portfolio?.map { it.toDomain(talentIdFallback ?: user.id) } ?: emptyList()
    return Pair(user, portfolio)
}

