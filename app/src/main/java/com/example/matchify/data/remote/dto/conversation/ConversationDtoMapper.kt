package com.example.matchify.data.remote.dto.conversation

import com.example.matchify.domain.model.Conversation

object ConversationDtoMapper {
    fun toDomain(dto: ConversationDto): Conversation {
        return Conversation(
            id = dto._id ?: dto.id,
            id_alt = null,
            missionId = dto.missionId,
            recruiterId = dto.recruiterId,
            talentId = dto.talentId,
            lastMessageText = dto.lastMessageText,
            lastMessageAt = dto.lastMessageAt,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            talentName = dto.talentName,
            talentProfileImage = dto.talentProfileImage,
            recruiterName = dto.recruiterName,
            recruiterProfileImage = dto.recruiterProfileImage,
            unreadCount = dto.unreadCount ?: 0
        )
    }
}

