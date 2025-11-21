package com.example.matchify.data.remote.dto.contract

import com.google.gson.annotations.SerializedName

data class CreateContractRequest(
    @SerializedName("missionId")
    val missionId: String,
    
    @SerializedName("talentId")
    val talentId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("paymentDetails")
    val paymentDetails: String? = null,
    
    @SerializedName("startDate")
    val startDate: String? = null,
    
    @SerializedName("endDate")
    val endDate: String? = null,
    
    @SerializedName("recruiterSignature")
    val recruiterSignature: String
)

