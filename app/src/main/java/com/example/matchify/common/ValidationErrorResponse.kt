package com.example.matchify.common

import com.google.gson.annotations.SerializedName

/**
 * Structure de réponse d'erreur de validation du backend NestJS
 * {
 *   "message": "Validation failed",
 *   "missingFields": string[],
 *   "fieldErrors": { [fieldName: string]: string }
 * }
 */
data class ValidationErrorResponse(
    @SerializedName("message") val message: String,
    @SerializedName("missingFields") val missingFields: List<String>? = null,
    @SerializedName("fieldErrors") val fieldErrors: Map<String, String>? = null
)

