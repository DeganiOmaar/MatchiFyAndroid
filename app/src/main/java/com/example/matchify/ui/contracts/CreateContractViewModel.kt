package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ContractRepository
import com.example.matchify.data.remote.dto.contract.CreateContractRequest
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import java.io.ByteArrayOutputStream

class CreateContractViewModel(
    private val repository: ContractRepository
) : ViewModel() {
    
    val title = MutableStateFlow("")
    val content = MutableStateFlow("")
    val paymentDetails = MutableStateFlow("")
    val startDate = MutableStateFlow<Long?>(null)
    val endDate = MutableStateFlow<Long?>(null)
    val signatureBitmap = MutableStateFlow<Bitmap?>(null)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _contractCreated = MutableStateFlow<Contract?>(null)
    val contractCreated: StateFlow<Contract?> = _contractCreated.asStateFlow()
    
    fun setSignature(bitmap: Bitmap) {
        signatureBitmap.value = bitmap
    }
    
    fun createContract(
        missionId: String,
        talentId: String,
        onSuccess: () -> Unit
    ) {
        if (title.value.isEmpty() || content.value.isEmpty() || signatureBitmap.value == null) {
            _errorMessage.value = "Veuillez remplir tous les champs requis"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val signatureBase64 = bitmapToBase64(signatureBitmap.value!!)
                
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val startDateStr = startDate.value?.let { dateFormatter.format(Date(it)) }
                val endDateStr = endDate.value?.let { dateFormatter.format(Date(it)) }
                
                val request = CreateContractRequest(
                    missionId = missionId,
                    talentId = talentId,
                    title = title.value,
                    content = content.value,
                    paymentDetails = if (paymentDetails.value.isNotEmpty()) paymentDetails.value else null,
                    startDate = startDateStr,
                    endDate = endDateStr,
                    recruiterSignature = "data:image/png;base64,$signatureBase64"
                )
                
                val contract = repository.createContract(request)
                _contractCreated.value = contract
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de la création du contrat"
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

class CreateContractViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateContractViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = ContractRepository(apiService.contractApi)
            @Suppress("UNCHECKED_CAST")
            return CreateContractViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

