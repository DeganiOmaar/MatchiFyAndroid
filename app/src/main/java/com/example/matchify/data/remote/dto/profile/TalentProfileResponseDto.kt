package com.example.matchify.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

/**
 * Réponse générique du backend pour le profil talent.
 *
 * ⚠️ Comme pour le recruteur, certains endpoints peuvent ne renvoyer que
 * un `message` sans l'objet `user`, donc `user` est nullable.
 */
data class TalentProfileResponseDto(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: TalentUserDto?
)

