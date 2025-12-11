package com.example.matchify.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

/**
 * Réponse générique du backend pour le profil recruteur.
 *
 * ⚠️ Certains endpoints (comme l'update) peuvent ne renvoyer qu'un `message`
 * sans objet `user`. C'est pourquoi `user` est nullable.
 */
data class RecruiterProfileResponseDto(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: RecruiterUserDto?
)

