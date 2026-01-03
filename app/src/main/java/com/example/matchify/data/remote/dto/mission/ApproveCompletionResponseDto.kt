package com.example.matchify.data.remote.dto.mission

import com.google.gson.annotations.SerializedName

data class ApproveCompletionResponseDto(
    @SerializedName("mission")
    val mission: MissionDto,
    @SerializedName("payment")
    val payment: PaymentIntentResponseDto?
)

data class PaymentIntentResponseDto(
    @SerializedName("clientSecret")
    val clientSecret: String,
    @SerializedName("paymentIntentId")
    val paymentIntentId: String,
    @SerializedName("customerId")
    val customerId: String?,
    @SerializedName("ephemeralKey")
    val ephemeralKey: String?,
    @SerializedName("publishableKey")
    val publishableKey: String?
)
