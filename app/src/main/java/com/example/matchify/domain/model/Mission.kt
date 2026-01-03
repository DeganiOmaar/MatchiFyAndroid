package com.example.matchify.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Mission(
    val id: String? = null,
    val _id: String? = null,
    val title: String = "",
    val description: String? = null,
    val duration: String? = null,
    val budget: Int? = null,
    val skills: List<String>? = null,
    val recruiterId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val proposalsCount: Int? = null,
    val interviewingCount: Int? = null,
    val unviewedCount: Int? = null,
    val hasApplied: Boolean? = null,
    val isFavorite: Boolean? = null,
    val status: String? = null,
    val matchScore: Int? = null,
    val reasoning: String? = null
) {
    val missionId: String
        get() = id ?: _id ?: ""

    val formattedDate: String
        get() {
            if (createdAt == null) return "-"
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(createdAt)
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                "-"
            }
        }

    val formattedBudget: String
        get() {
            // Format matching screenshot: "€500", "€1200", "€350" (no spaces, euro symbol first)
            return budget?.let { "€$it" } ?: "€0"
        }
    
    val proposals: Int
        get() = proposalsCount ?: 0
    
    val interviewing: Int
        get() = interviewingCount ?: 0
    
    val hasAppliedToMission: Boolean
        get() = hasApplied ?: false
    
    val isFavorited: Boolean
        get() = isFavorite ?: false
    
    val missionStatus: MissionStatus
        get() = when (status) {
            "started" -> MissionStatus.STARTED
            "completed" -> MissionStatus.COMPLETED
            "paid" -> MissionStatus.PAID
            else -> MissionStatus.IN_PROGRESS
        }
}

enum class MissionStatus(val displayName: String) {
    IN_PROGRESS("En cours"),
    STARTED("Démarrée"),
    COMPLETED("Terminée"),
    PAID("Payée")
}


