package com.example.matchify.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

data class CreateCheckoutSessionRequest(
    @SerializedName("successUrl") val successUrl: String,
    @SerializedName("cancelUrl") val cancelUrl: String
)

data class CheckoutSessionResponse(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("checkoutUrl") val checkoutUrl: String
)
