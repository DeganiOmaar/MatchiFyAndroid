package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

data class UpdateProposalStatusRequest(
    @SerializedName("status") val status: String, // "ACCEPTED" or "REFUSED"
    @SerializedName("rejectionReason") val rejectionReason: String? = null
) {
    // Custom serialization to exclude null rejectionReason
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>("status" to status)
        rejectionReason?.let { map["rejectionReason"] = it }
        return map
    }
}

