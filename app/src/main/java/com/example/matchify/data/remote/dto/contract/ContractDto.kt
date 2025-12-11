package com.example.matchify.data.remote.dto.contract

import com.example.matchify.domain.model.Contract
import com.google.gson.annotations.SerializedName

enum class ContractStatus {
    @SerializedName("sent_to_talent")
    SENT_TO_TALENT,
    
    @SerializedName("declined_by_talent")
    DECLINED_BY_TALENT,
    
    @SerializedName("signed_by_both")
    SIGNED_BY_BOTH
}

data class ContractDto(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("_id")
    val _id: String? = null,
    
    @SerializedName("missionId")
    val missionId: String,
    
    @SerializedName("recruiterId")
    val recruiterId: String,
    
    @SerializedName("talentId")
    val talentId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("scope")
    val scope: String,
    
    @SerializedName("budget")
    val budget: String,
    
    @SerializedName("paymentDetails")
    val paymentDetails: String? = null,
    
    @SerializedName("startDate")
    val startDate: String? = null,
    
    @SerializedName("endDate")
    val endDate: String? = null,
    
    @SerializedName("recruiterSignature")
    val recruiterSignature: String,
    
    @SerializedName("talentSignature")
    val talentSignature: String? = null,
    
    @SerializedName("status")
    val status: ContractStatus,
    
    @SerializedName("pdfUrl")
    val pdfUrl: String? = null,
    
    @SerializedName("signedPdfUrl")
    val signedPdfUrl: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

fun ContractDto.toDomain(): Contract {
    return Contract(
        id = _id ?: id ?: "",
        missionId = missionId,
        recruiterId = recruiterId,
        talentId = talentId,
        title = title,
        content = content,
        scope = scope,
        budget = budget,
        paymentDetails = paymentDetails,
        startDate = startDate,
        endDate = endDate,
        recruiterSignature = recruiterSignature,
        talentSignature = talentSignature,
        status = when (status) {
            ContractStatus.SENT_TO_TALENT -> Contract.ContractStatus.SENT_TO_TALENT
            ContractStatus.DECLINED_BY_TALENT -> Contract.ContractStatus.DECLINED_BY_TALENT
            ContractStatus.SIGNED_BY_BOTH -> Contract.ContractStatus.SIGNED_BY_BOTH
        },
        pdfUrl = pdfUrl,
        signedPdfUrl = signedPdfUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

