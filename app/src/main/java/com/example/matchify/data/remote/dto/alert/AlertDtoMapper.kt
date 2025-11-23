package com.example.matchify.data.remote.dto.alert

import com.example.matchify.domain.model.Alert

fun AlertDto.toDomain(): Alert {
    return Alert(
        id = alertId,
        title = title,
        message = message,
        isRead = isRead,
        proposalId = proposalId,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt
    )
}

