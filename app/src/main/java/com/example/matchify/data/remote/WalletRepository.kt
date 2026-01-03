package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.wallet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WalletRepository(private val api: WalletApi) {
    
    suspend fun getWalletSummary(): WalletSummaryDto = withContext(Dispatchers.IO) {
        api.getWalletSummary()
    }

    suspend fun getTransactions(
        page: Int? = null,
        limit: Int? = null,
        status: String? = null
    ): TransactionListResponseDto = withContext(Dispatchers.IO) {
        api.getTransactions(page, limit, status)
    }

    suspend fun getTransactionDetails(id: String): PaymentTransactionDto = withContext(Dispatchers.IO) {
        api.getTransactionDetails(id)
    }

    suspend fun requestPayout(amount: Double): Any = withContext(Dispatchers.IO) {
        api.requestPayout(mapOf("amount" to amount))
    }

    suspend fun getPaymentMethods(): List<PaymentMethodDto> = withContext(Dispatchers.IO) {
        api.getPaymentMethods()
    }
}
