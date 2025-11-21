package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ContractRepository
import com.example.matchify.data.remote.dto.contract.SignContractRequest
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Base64
import java.io.ByteArrayOutputStream

class ContractReviewViewModel(
    private val contractId: String,
    private val repository: ContractRepository
) : ViewModel() {
    
    private val _contract = MutableStateFlow<Contract?>(null)
    val contract: StateFlow<Contract?> = _contract.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun loadContract() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val contract = repository.getContractById(contractId)
                _contract.value = contract
            } catch (e: Exception) {
                // Silently fail - use initial contract if available
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun signContract(
        signature: Bitmap,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val signatureBase64 = bitmapToBase64(signature)
                val request = SignContractRequest(
                    talentSignature = "data:image/png;base64,$signatureBase64"
                )
                
                val signedContract = repository.signContract(contractId, request)
                _contract.value = signedContract
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Erreur lors de la signature du contrat"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun declineContract(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val declinedContract = repository.declineContract(contractId)
                _contract.value = declinedContract
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Erreur lors du refus du contrat"
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

class ContractReviewViewModelFactory(
    private val contractId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContractReviewViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = ContractRepository(apiService.contractApi)
            @Suppress("UNCHECKED_CAST")
            return ContractReviewViewModel(contractId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

