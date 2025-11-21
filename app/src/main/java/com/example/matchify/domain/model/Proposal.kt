package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

enum class ProposalStatus(val value: String, val displayName: String) {
    @SerializedName("NOT_VIEWED")
    NOT_VIEWED("NOT_VIEWED", "Not viewed"),
    
    @SerializedName("VIEWED")
    VIEWED("VIEWED", "Viewed"),
    
    @SerializedName("ACCEPTED")
    ACCEPTED("ACCEPTED", "Accepted"),
    
    @SerializedName("REFUSED")
    REFUSED("REFUSED", "Refused")
}

data class Proposal(
    @SerializedName("_id") val id: String? = null,
    val id_alt: String? = null,
    @SerializedName("missionId") val missionId: String,
    @SerializedName("missionTitle") val missionTitle: String? = null,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("talentName") val talentName: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("status") val status: ProposalStatus,
    @SerializedName("message") val message: String,
    @SerializedName("proposedBudget") val proposedBudget: Int? = null,
    @SerializedName("estimatedDuration") val estimatedDuration: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    val proposalId: String
        get() = id ?: id_alt ?: UUID.randomUUID().toString()
    
    val formattedDate: String
        get() {
            if (createdAt == null) return "-"
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(createdAt)
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.FRENCH)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "-"
            }
        }
}

