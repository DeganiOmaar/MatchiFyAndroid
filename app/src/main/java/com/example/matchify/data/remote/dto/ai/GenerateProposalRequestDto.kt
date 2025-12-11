package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * Requête pour générer une proposition avec AI
 */
data class GenerateProposalRequestDto(
    @SerializedName("missionId") val missionId: String
)

/**
 * Réponse de génération de proposition AI
 */
data class GenerateProposalResponseDto(
    @SerializedName("proposalContent") val proposalContent: String
)


