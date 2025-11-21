package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.contract.ContractDto
import com.example.matchify.data.remote.dto.contract.CreateContractRequest
import com.example.matchify.data.remote.dto.contract.SignContractRequest
import retrofit2.http.*

interface ContractApi {
    
    @POST("contracts")
    suspend fun createContract(@Body request: CreateContractRequest): ContractDto
    
    @GET("contracts/{id}")
    suspend fun getContractById(@Path("id") id: String): ContractDto
    
    @GET("contracts/conversation/{conversationId}")
    suspend fun getContractsByConversation(@Path("conversationId") conversationId: String): List<ContractDto>
    
    @PATCH("contracts/{id}/sign")
    suspend fun signContract(
        @Path("id") id: String,
        @Body request: SignContractRequest
    ): ContractDto
    
    @PATCH("contracts/{id}/decline")
    suspend fun declineContract(@Path("id") id: String): ContractDto
}

