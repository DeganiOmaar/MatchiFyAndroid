package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.contract.ContractDto
import com.example.matchify.data.remote.dto.contract.CreateContractRequest
import com.example.matchify.data.remote.dto.contract.SignContractRequest
import com.example.matchify.data.remote.dto.contract.toDomain
import com.example.matchify.domain.model.Contract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContractRepository(
    private val api: ContractApi
) {
    
    suspend fun createContract(request: CreateContractRequest): Contract = withContext(Dispatchers.IO) {
        api.createContract(request).toDomain()
    }
    
    suspend fun getContractById(id: String): Contract = withContext(Dispatchers.IO) {
        api.getContractById(id).toDomain()
    }
    
    suspend fun getContractsByConversation(conversationId: String): List<Contract> = withContext(Dispatchers.IO) {
        api.getContractsByConversation(conversationId).map { it.toDomain() }
    }
    
    suspend fun signContract(id: String, request: SignContractRequest): Contract = withContext(Dispatchers.IO) {
        api.signContract(id, request).toDomain()
    }
    
    suspend fun declineContract(id: String): Contract = withContext(Dispatchers.IO) {
        api.declineContract(id).toDomain()
    }
}

