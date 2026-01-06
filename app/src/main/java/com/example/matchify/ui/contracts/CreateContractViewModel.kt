package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.common.ValidationErrorResponse
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ContractRepository
import com.example.matchify.data.remote.dto.contract.CreateContractRequest
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import android.util.Base64
import java.io.ByteArrayOutputStream

class CreateContractViewModel(
    private val repository: ContractRepository
) : ViewModel() {
    
    val title = MutableStateFlow("")
    val scope = MutableStateFlow("")
    val budget = MutableStateFlow("")
    val paymentDetails = MutableStateFlow("")
    val startDate = MutableStateFlow(System.currentTimeMillis())
    val endDate = MutableStateFlow(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000))  // 30 days from now
    val signatureBitmap = MutableStateFlow<Bitmap?>(null)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // Erreurs de validation par champ (fieldErrors du backend)
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors
    
    private val _contractCreated = MutableStateFlow<Contract?>(null)
    val contractCreated: StateFlow<Contract?> = _contractCreated.asStateFlow()
    
    /**
     * Récupère le message d'erreur pour un champ spécifique
     */
    fun getFieldError(fieldName: String): String? {
        return _fieldErrors.value[fieldName]
    }
    
    fun setSignature(bitmap: Bitmap) {
        signatureBitmap.value = bitmap
    }
    
    fun createContract(
        missionId: String,
        talentId: String,
        onSuccess: () -> Unit
    ) {
        // Validation des champs requis
        when {
            title.value.trim().isEmpty() -> {
                _errorMessage.value = "Le titre du contrat est requis"
                return
            }
            scope.value.trim().isEmpty() -> {
                _errorMessage.value = "Le contenu du projet (scope) est requis"
                return
            }
            budget.value.trim().isEmpty() -> {
                _errorMessage.value = "Le budget est requis"
                return
            }
            signatureBitmap.value == null -> {
                _errorMessage.value = "La signature est requise"
                return
            }
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _fieldErrors.value = emptyMap()
            
            try {
                val signatureBase64 = bitmapToBase64(signatureBitmap.value!!)
                
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val startDateStr = dateFormatter.format(Date(startDate.value))
                val endDateStr = dateFormatter.format(Date(endDate.value))
                
                val request = CreateContractRequest(
                    missionId = missionId,
                    talentId = talentId,
                    title = title.value.trim(),
                    content = scope.value.trim(), // Map scope to content
                    startDate = startDateStr,
                    endDate = endDateStr,
                    paymentDetails = "Budget: ${budget.value.trim()}\n${if (paymentDetails.value.isNotEmpty()) paymentDetails.value.trim() else ""}".trim(),
                    recruiterSignature = "data:image/png;base64,$signatureBase64"
                )
                
                val contract = repository.createContract(request)
                _contractCreated.value = contract
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("CreateContractViewModel", "Error creating contract: ${e.message}", e)
                
                // Extraire les erreurs de validation structurées si c'est une HttpException
                if (e is HttpException) {
                    val code = e.code()
                    android.util.Log.d("CreateContractViewModel", "HTTP Error code: $code")
                    
                    val validationErrors = ErrorHandler.extractValidationErrors(e)
                    android.util.Log.d("CreateContractViewModel", "Validation errors extracted: $validationErrors")
                    
                    if (validationErrors != null && !validationErrors.fieldErrors.isNullOrEmpty()) {
                        // Mapper les noms de champs du backend vers les noms du frontend
                        val mappedErrors = mutableMapOf<String, String>()
                        validationErrors.fieldErrors.forEach { (fieldName, errorMsg) ->
                            // Mapper les noms de champs possibles
                            val mappedFieldName = when (fieldName.lowercase()) {
                                "contracttitle", "contract_title" -> "title"
                                "projectscope", "project_scope", "content" -> "scope"
                                "budgetandpaymentterms", "budget_and_payment_terms" -> "budget"
                                "paymentdetails", "payment_details" -> "paymentDetails"
                                "startdate", "start_date" -> "startDate"
                                "enddate", "end_date" -> "endDate"
                                "recruitersignature", "recruiter_signature" -> "recruiterSignature"
                                else -> fieldName.lowercase()
                            }
                            mappedErrors[mappedFieldName] = errorMsg
                        }
                        
                        // Afficher les erreurs par champ
                        _fieldErrors.value = mappedErrors
                        // Message général si disponible (mais ne pas l'afficher si on a des fieldErrors)
                        _errorMessage.value = null // Ne pas afficher le message général si on a des erreurs par champ
                        android.util.Log.d("CreateContractViewModel", "Field errors set: ${_fieldErrors.value}")
                    } else {
                        // Message d'erreur générique avec ErrorHandler
                        val errorMsg = ErrorHandler.getErrorMessage(e, ErrorContext.CONTRACT_CREATE)
                        _errorMessage.value = errorMsg
                        _fieldErrors.value = emptyMap()
                        android.util.Log.d("CreateContractViewModel", "Generic error message: $errorMsg")
                    }
                } else {
                    _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.CONTRACT_CREATE)
                    _fieldErrors.value = emptyMap()
                }
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

