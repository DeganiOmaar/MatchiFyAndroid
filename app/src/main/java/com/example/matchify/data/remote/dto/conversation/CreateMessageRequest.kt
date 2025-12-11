package com.example.matchify.data.remote.dto.conversation

import com.google.gson.annotations.SerializedName

data class CreateMessageRequest(
    @SerializedName("text") val text: String
)

