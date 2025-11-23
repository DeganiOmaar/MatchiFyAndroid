package com.example.matchify.domain.model

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val proposalId: String,
    val profileImageUrl: String? = null,
    val createdAt: String? = null
)

