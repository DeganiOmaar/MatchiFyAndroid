package com.example.matchify.data.remote.dto.alert

import com.google.gson.annotations.SerializedName

data class AlertDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("proposalId") val proposalId: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
) {
    val alertId: String
        get() = _id ?: id ?: ""
}

