package com.example.matchify.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.WalletRepository
import com.example.matchify.data.remote.dto.wallet.PaymentTransactionDto
import com.example.matchify.data.remote.dto.wallet.WalletSummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    private val _summary = MutableStateFlow<WalletSummaryDto?>(null)
    val summary: StateFlow<WalletSummaryDto?> = _summary.asStateFlow()

    private val _transactions = MutableStateFlow<List<PaymentTransactionDto>>(emptyList())
    val transactions: StateFlow<List<PaymentTransactionDto>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _onboardingUrl = MutableStateFlow<String?>(null)
    val onboardingUrl: StateFlow<String?> = _onboardingUrl.asStateFlow()

    private var currentPage = 1
    private var hasMorePages = true

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _summary.value = repository.getWalletSummary()
                currentPage = 1
                val response = repository.getTransactions(page = currentPage, limit = 20)
                _transactions.value = response.transactions
                hasMorePages = currentPage < response.pages
                currentPage++
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (_isLoading.value || !hasMorePages) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getTransactions(page = currentPage, limit = 20)
                _transactions.value = _transactions.value + response.transactions
                hasMorePages = currentPage < response.pages
                currentPage++
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun linkStripeAccount() {
        // Implementation for linking stripe account
        // Usually returns an onboarding URL
        viewModelScope.launch {
             // Mock call for now or use repository if endpoint exists
             // _onboardingUrl.value = "https://connect.stripe.com/..."
        }
    }

    fun clearOnboardingUrl() {
        _onboardingUrl.value = null
    }
}

class WalletViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = WalletRepository(apiService.walletApi)
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
