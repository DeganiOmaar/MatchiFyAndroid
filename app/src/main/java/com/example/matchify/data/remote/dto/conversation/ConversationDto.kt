package com.example.matchify.data.remote.dto.conversation

import com.google.gson.annotations.SerializedName

data class ConversationDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("missionId") val missionId: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("lastMessageText") val lastMessageText: String? = null,
    @SerializedName("lastMessageAt") val lastMessageAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("talentName") val talentName: String? = null,
    @SerializedName("talentProfileImage") val talentProfileImage: String? = null,
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("recruiterProfileImage") val recruiterProfileImage: String? = null,
    @SerializedName("unreadCount") val unreadCount: Int? = null
)

