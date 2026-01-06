package com.example.matchify.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

data class UpdateInterviewRequest(
    @SerializedName("scheduledAt") val scheduledAt: String? = null, // ISO 8601 format
    @SerializedName("meetLink") val meetLink: String? = null,
    @SerializedName("status") val status: String? = null, // "SCHEDULED" | "COMPLETED" | "CANCELLED"
    @SerializedName("notes") val notes: String? = null
)

