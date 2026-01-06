package com.example.matchify.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

data class CancelInterviewRequest(
    @SerializedName("cancellationReason") val cancellationReason: String
)

