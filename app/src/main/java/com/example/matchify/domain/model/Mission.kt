package com.example.matchify.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Mission(
    val id: String? = null,
    val _id: String? = null,
    val title: String,
    val description: String,
    val duration: String,
    val budget: Int,
    val skills: List<String>,
    val recruiterId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val proposalsCount: Int? = null,
    val interviewingCount: Int? = null,
    val hasApplied: Boolean? = null,
    val isFavorite: Boolean? = null,
    val status: String? = null
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
            val formatter = java.text.NumberFormat.getNumberInstance(Locale.FRENCH)
            return "${formatter.format(budget)} €"
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
            else -> MissionStatus.IN_PROGRESS
        }
}

enum class MissionStatus(val displayName: String) {
    IN_PROGRESS("En cours"),
    STARTED("Démarrée"),
    COMPLETED("Terminée")
}


