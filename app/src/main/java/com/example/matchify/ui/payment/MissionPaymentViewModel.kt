package com.example.matchify.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.remote.PaymentRepository
import com.example.matchify.data.remote.dto.mission.toDomain
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MissionPaymentViewModel(
    private val missionId: String,
    private val missionRepository: MissionRepository,
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }

    init {
        loadMission()
    }

    fun loadMission() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val loadedMission = missionRepository.getMissionById(missionId)
                _mission.value = loadedMission
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to load mission: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeMission() {
        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null
            try {
                val updatedMission = missionRepository.markAsCompleted(missionId)
                _mission.value = updatedMission
                _successMessage.value = "Mission marked as complete! The recruiter will be notified."
                _isSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to mark as complete: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    private val _paymentConfig = MutableStateFlow<PaymentConfig?>(null)
    val paymentConfig: StateFlow<PaymentConfig?> = _paymentConfig.asStateFlow()

    private val _checkoutUrl = MutableStateFlow<String?>(null)
    val checkoutUrl: StateFlow<String?> = _checkoutUrl.asStateFlow()

    fun initiateWebViewPayment() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("MissionPaymentViewModel", "Creating checkout session for mission: $missionId")
                val response = paymentRepository.createCheckoutSession(missionId)
                _checkoutUrl.value = response.checkoutUrl
                android.util.Log.d("MissionPaymentViewModel", "Checkout URL received: ${response.checkoutUrl}")
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to create checkout session: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initiatePayment() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                if (isRecruiter) {
                    android.util.Log.d("MissionPaymentViewModel", "Initiating payment for recruiter, missionId: $missionId")
                    val response = missionRepository.approveCompletion(missionId)
                    val payment = response.payment
                    if (payment != null) {
                        android.util.Log.d("MissionPaymentViewModel", "Payment details received: ${payment.paymentIntentId}")
                        _paymentConfig.value = PaymentConfig(
                            paymentIntentClientSecret = payment.clientSecret,
                            ephemeralKey = payment.ephemeralKey ?: "",
                            customerId = payment.customerId ?: "",
                            publishableKey = payment.publishableKey ?: "",
                            paymentIntentId = payment.paymentIntentId
                        )
                        // Update mission status to paid locally (backend already set it to paid)
                        _mission.value = response.mission.toDomain()
                    } else if (response.mission.status == "paid") {
                        // Already paid case
                        _mission.value = response.mission.toDomain()
                        _successMessage.value = "Mission is already paid."
                        _isSuccess.value = true
                    } else {
                        android.util.Log.e("MissionPaymentViewModel", "No payment details in response. Status: ${response.mission.status}")
                        _errorMessage.value = "Failed to initialize payment: No payment details returned"
                    }
                } else {
                    android.util.Log.d("MissionPaymentViewModel", "Initiating payment intent for talent, missionId: $missionId")
                    val response = paymentRepository.createPaymentIntent(missionId)
                    _paymentConfig.value = PaymentConfig(
                        paymentIntentClientSecret = response.clientSecret,
                        ephemeralKey = response.ephemeralKey,
                        customerId = response.customerId ?: "",
                        publishableKey = response.publishableKey,
                        paymentIntentId = response.paymentIntentId
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to initialize payment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onPaymentSuccess() {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                 val paymentIntentId = _paymentConfig.value?.paymentIntentId
                 if (paymentIntentId != null) {
                     android.util.Log.d("MissionPaymentViewModel", "Confirming payment: $paymentIntentId")
                     paymentRepository.confirmPayment(paymentIntentId, missionId)
                     _successMessage.value = "Payment successful! The funds have been transferred."
                     _isSuccess.value = true
                     // Reload mission to show new status
                     loadMission()
                 } else {
                     android.util.Log.e("MissionPaymentViewModel", "Payment confirmed but ID missing in config")
                     _errorMessage.value = "Payment confirmed but ID missing"
                 }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Backend confirmation failed: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    data class PaymentConfig(
        val paymentIntentClientSecret: String,
        val ephemeralKey: String,
        val customerId: String,
        val publishableKey: String,
        val paymentIntentId: String = ""
    )
}

class MissionPaymentViewModelFactory(
    private val missionId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        android.util.Log.d("MissionPaymentViewModelFactory", "Creating MissionPaymentViewModel for missionId: $missionId")
        if (modelClass.isAssignableFrom(MissionPaymentViewModel::class.java)) {
            try {
                val prefs = AuthPreferencesProvider.getInstance().get()
                val apiService = ApiService.getInstance()
                val repository = MissionRepository(apiService.missionApi, prefs)
                val paymentRepository = PaymentRepository(apiService.paymentApi)
                @Suppress("UNCHECKED_CAST")
                return MissionPaymentViewModel(missionId, repository, paymentRepository) as T
            } catch (e: Exception) {
                android.util.Log.e("MissionPaymentViewModelFactory", "Error creating ViewModel", e)
                throw e
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
