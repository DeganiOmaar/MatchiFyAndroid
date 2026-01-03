package com.example.matchify.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

data class CreatePaymentIntentRequest(
    @SerializedName("missionId") val missionId: String,
    @SerializedName("paymentMethodId") val paymentMethodId: String? = null
)

data class CreatePaymentIntentResponse(
    @SerializedName("clientSecret") val clientSecret: String,
    @SerializedName("paymentIntentId") val paymentIntentId: String,
    @SerializedName("customerId") val customerId: String?,
    @SerializedName("ephemeralKey") val ephemeralKey: String,
    @SerializedName("publishableKey") val publishableKey: String
)

data class ConfirmPaymentRequest(
    @SerializedName("paymentIntentId") val paymentIntentId: String,
    @SerializedName("missionId") val missionId: String
)

data class ConnectAccountResponse(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("onboardingUrl") val onboardingUrl: String
)

data class ConnectAccountStatusResponse(
    @SerializedName("verified") val verified: Boolean,
    @SerializedName("chargesEnabled") val chargesEnabled: Boolean,
    @SerializedName("payoutsEnabled") val payoutsEnabled: Boolean
)
