package com.example.matchify.data.remote.dto.wallet

import com.google.gson.annotations.SerializedName

// MARK: - Wallet Summary
data class WalletSummaryDto(
    @SerializedName("userId") val userId: String,
    @SerializedName("role") val role: String,
    @SerializedName("availableBalance") val availableBalance: Double,
    @SerializedName("pendingBalance") val pendingBalance: Double,
    @SerializedName("totalEarned") val totalEarned: Double,
    @SerializedName("totalSpent") val totalSpent: Double,
    @SerializedName("stripeCustomerId") val stripeCustomerId: String?,
    @SerializedName("stripeConnectAccountId") val stripeConnectAccountId: String?
)

// MARK: - Transaction List Response
data class TransactionListResponseDto(
    @SerializedName("transactions") val transactions: List<PaymentTransactionDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("pages") val pages: Int
)

// MARK: - Payment Transaction
data class PaymentTransactionDto(
    @SerializedName("_id") val id: String,
    @SerializedName("missionId") val missionId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("platformFee") val platformFee: Double,
    @SerializedName("talentAmount") val talentAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("direction") val direction: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("completedAt") val completedAt: String?
)

// MARK: - Payout Model
data class PayoutDto(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("arrivalDate") val arrivalDate: String?
)

// MARK: - Payout Status Response
data class PayoutStatusResponseDto(
    @SerializedName("hasConnectAccount") val hasConnectAccount: Boolean,
    @SerializedName("payouts") val payouts: List<PayoutDto>?
)

// MARK: - Payment Method
data class PaymentMethodDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("last4") val last4: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("isDefault") val isDefault: Boolean
)
