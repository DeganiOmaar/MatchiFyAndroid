package com.example.matchify.data.remote.dto.contract

import com.google.gson.annotations.SerializedName

data class SignContractRequest(
    @SerializedName("talentSignature")
    val talentSignature: String
)

