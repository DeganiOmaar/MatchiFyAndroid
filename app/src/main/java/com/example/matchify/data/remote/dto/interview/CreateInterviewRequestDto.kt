package com.example.matchify.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

data class CreateInterviewRequestDto(
    @SerializedName("proposalId")
    val proposalId: String,
    
    @SerializedName("scheduledAt")
    val scheduledAt: String, // ISO 8601 combined date time
    
    @SerializedName("meetLink")
    val meetLink: String? = null,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("autoGenerateMeetLink")
    val autoGenerateMeetLink: Boolean = true
)
