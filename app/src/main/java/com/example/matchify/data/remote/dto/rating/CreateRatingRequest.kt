package com.example.matchify.data.remote.dto.rating

import com.google.gson.annotations.SerializedName

/**
 * DTO pour créer ou mettre à jour un rating
 * 
 * Schéma backend :
 * - talentId (obligatoire, ref User)
 * - recruiterId (géré automatiquement par le backend)
 * - missionId (optionnel)
 * - score (obligatoire, 1-5)
 * - comment (optionnel)
 * 
 * Champs supprimés : recommended, tags
 */
data class CreateRatingRequest(
    @SerializedName("talentId")
    val talentId: String, // obligatoire
    
    @SerializedName("missionId")
    val missionId: String? = null, // optionnel, mais à envoyer si le rating est lié à une mission
    
    @SerializedName("score")
    val score: Int, // entier de 1 à 5
    
    @SerializedName("comment")
    val comment: String? = null // texte optionnel max 1000 caractères
)

