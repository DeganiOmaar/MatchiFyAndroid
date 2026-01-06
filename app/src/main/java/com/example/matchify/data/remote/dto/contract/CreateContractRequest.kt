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
    val content: String, // Le backend attend "content" pour le scope du projet
    
    @SerializedName("startDate")
    val startDate: String,
    
    @SerializedName("endDate")
    val endDate: String,
    
    @SerializedName("paymentDetails")
    val paymentDetails: String? = null,
    
    @SerializedName("recruiterSignature")
    val recruiterSignature: String
)

