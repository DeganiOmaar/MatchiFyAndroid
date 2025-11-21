package com.example.matchify.ui.contracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ContractRepository
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContractDetailViewModel(
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
            _errorMessage.value = null
            
            try {
                val contract = repository.getContractById(contractId)
                _contract.value = contract
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement du contrat"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ContractDetailViewModelFactory(
    private val contractId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContractDetailViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = ContractRepository(apiService.contractApi)
            @Suppress("UNCHECKED_CAST")
            return ContractDetailViewModel(contractId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

