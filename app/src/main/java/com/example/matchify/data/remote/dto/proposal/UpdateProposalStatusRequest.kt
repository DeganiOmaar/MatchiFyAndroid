package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

data class UpdateProposalStatusRequest(
    @SerializedName("status") val status: String // "ACCEPTED" or "REFUSED"
)

