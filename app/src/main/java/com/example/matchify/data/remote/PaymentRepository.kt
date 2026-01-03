package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.payment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository(private val api: PaymentApi) {
    
    suspend fun createPaymentIntent(missionId: String): CreatePaymentIntentResponse = withContext(Dispatchers.IO) {
        api.createPaymentIntent(CreatePaymentIntentRequest(missionId = missionId))
    }

    suspend fun confirmPayment(paymentIntentId: String, missionId: String) = withContext(Dispatchers.IO) {
        api.confirmPayment(ConfirmPaymentRequest(paymentIntentId = paymentIntentId, missionId = missionId))
    }

    suspend fun createCheckoutSession(missionId: String): CheckoutSessionResponse = withContext(Dispatchers.IO) {
        api.createCheckoutSession(
            missionId,
            CreateCheckoutSessionRequest(
                successUrl = "matchify://payment/success",
                cancelUrl = "matchify://payment/cancel"
            )
        )
    }

    suspend fun createConnectAccount(): ConnectAccountResponse = withContext(Dispatchers.IO) {
        api.createConnectAccount()
    }

    suspend fun getConnectStatus(accountId: String): ConnectAccountStatusResponse = withContext(Dispatchers.IO) {
        api.getConnectStatus(accountId)
    }
}
