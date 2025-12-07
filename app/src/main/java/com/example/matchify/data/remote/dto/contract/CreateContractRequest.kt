package com.example.matchify.data.remote.dto.contract

import com.google.gson.annotations.SerializedName

data class CreateContractRequest(
    @SerializedName("missionId")
    val missionId: String,
    
    @SerializedName("talentId")
    val talentId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("scope")
    val scope: String,
    
    @SerializedName("budget")
    val budget: String,
    
    @SerializedName("startDate")
    val startDate: String,
    
    @SerializedName("endDate")
    val endDate: String,
    
    @SerializedName("paymentDetails")
    val paymentDetails: String? = null,
    
    @SerializedName("recruiterSignature")
    val recruiterSignature: String
)

