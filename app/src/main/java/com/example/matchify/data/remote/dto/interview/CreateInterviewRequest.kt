package com.example.matchify.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

data class CreateInterviewRequest(
    @SerializedName("proposalId") val proposalId: String,
    @SerializedName("scheduledAt") val scheduledAt: String, // ISO 8601 format
    @SerializedName("meetLink") val meetLink: String? = null, // Optionnel si autoGenerateMeetLink = true
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("autoGenerateMeetLink") val autoGenerateMeetLink: Boolean = false
)

