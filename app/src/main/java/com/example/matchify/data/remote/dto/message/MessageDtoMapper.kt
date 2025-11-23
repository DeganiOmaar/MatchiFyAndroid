package com.example.matchify.data.remote.dto.message

import com.example.matchify.domain.model.Message

object MessageDtoMapper {
    fun toDomain(dto: MessageDto): Message {
        return Message(
            id = dto._id ?: dto.id,
            id_alt = null,
            conversationId = dto.conversationId,
            senderId = dto.senderId,
            senderRole = dto.senderRole,
            text = dto.text,
            contentParam = dto.content,
            contractId = dto.contractId,
            pdfUrl = dto.pdfUrl,
            isContractMessage = dto.isContractMessage,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}

