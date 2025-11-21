package com.example.matchify.domain.model

data class Contract(
    val id: String,
    val missionId: String,
    val recruiterId: String,
    val talentId: String,
    val title: String,
    val content: String,
    val paymentDetails: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val recruiterSignature: String,
    val talentSignature: String? = null,
    val status: ContractStatus,
    val pdfUrl: String? = null,
    val signedPdfUrl: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    enum class ContractStatus {
        SENT_TO_TALENT,
        DECLINED_BY_TALENT,
        SIGNED_BY_BOTH
    }
    
    val contractId: String
        get() = id
}

