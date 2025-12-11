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

data class TalentInfo(
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null
)

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
    @SerializedName("proposalContent") val proposalContent: String? = null,
    @SerializedName("proposedBudget") val proposedBudget: Int? = null,
    @SerializedName("estimatedDuration") val estimatedDuration: String? = null,
    @SerializedName("archived") val archived: Boolean? = null,
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
    @SerializedName("talent") val talent: TalentInfo? = null,
    @SerializedName("aiScore") val aiScore: Int? = null,
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
    
    val isArchived: Boolean
        get() = archived ?: false

    val talentFullName: String
        get() {
            // First check nested talent object for fullName (most reliable)
            if (!talent?.fullName.isNullOrEmpty() && talent?.fullName?.contains("@") == false) {
                return talent?.fullName!!
            }
            // Then check talentName, but only if it doesn't look like an email
            if (!talentName.isNullOrEmpty()) {
                // If it contains @, it's an email, so ignore it completely (return "Talent")
                if (talentName.contains("@")) {
                    return "Talent"
                }
                return talentName
            }
            return "Talent"
        }
}

