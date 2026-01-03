package com.example.matchify.data.remote.dto.deliverable

import com.example.matchify.domain.model.Deliverable

fun DeliverableDto.toDomain(): Deliverable {
    return Deliverable(
        id = id,
        messageId = messageId,
        missionId = missionId,
        senderId = senderId,
        receiverId = receiverId,
        fileUrl = fileUrl,
        fileName = fileName,
        fileSize = fileSize,
        fileType = fileType,
        type = type,
        url = url,
        status = status,
        rejectionReason = rejectionReason,
        approvedAt = approvedAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
