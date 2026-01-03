package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.wallet.*
import retrofit2.http.*

interface WalletApi {
    @GET("wallet/summary")
    suspend fun getWalletSummary(): WalletSummaryDto

    @GET("wallet/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("status") status: String? = null
    ): TransactionListResponseDto

    @GET("wallet/transactions/{id}")
    suspend fun getTransactionDetails(@Path("id") id: String): PaymentTransactionDto

    @GET("wallet/payment-methods")
    suspend fun getPaymentMethods(): List<PaymentMethodDto>

    @POST("wallet/payout")
    suspend fun requestPayout(@Body body: Map<String, Double>): Any
}
