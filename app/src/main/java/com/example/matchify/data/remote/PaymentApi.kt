package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.payment.CreatePaymentIntentRequest
import com.example.matchify.data.remote.dto.payment.CreatePaymentIntentResponse
import com.example.matchify.data.remote.dto.payment.ConfirmPaymentRequest
import com.example.matchify.data.remote.dto.payment.ConnectAccountResponse
import com.example.matchify.data.remote.dto.payment.ConnectAccountStatusResponse
import com.example.matchify.data.remote.dto.payment.CreateCheckoutSessionRequest
import com.example.matchify.data.remote.dto.payment.CheckoutSessionResponse
import retrofit2.http.*

interface PaymentApi {
    @POST("payment/create-intent")
    suspend fun createPaymentIntent(@Body request: CreatePaymentIntentRequest): CreatePaymentIntentResponse

    @POST("payment/confirm")
    suspend fun confirmPayment(@Body request: ConfirmPaymentRequest): Any

    @POST("payment/create-checkout-session/{missionId}")
    suspend fun createCheckoutSession(
        @Path("missionId") missionId: String,
        @Body request: CreateCheckoutSessionRequest
    ): CheckoutSessionResponse

    @POST("payment/connect/create")
    suspend fun createConnectAccount(): ConnectAccountResponse

    @GET("payment/connect/status/{accountId}")
    suspend fun getConnectStatus(@Path("accountId") accountId: String): ConnectAccountStatusResponse
}
