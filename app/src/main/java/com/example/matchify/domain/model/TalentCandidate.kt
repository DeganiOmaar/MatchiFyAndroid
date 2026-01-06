package com.example.matchify.domain.model

/**
 * Modèle domaine pour un candidat talent avec score IA
 * Correspond à TalentCandidateDto côté API
 */
data class TalentCandidate(
    val talentId: String,
    val score: Int, // Score IA (0-100)
    val reasons: String? = null, // Raisons du score
    val matchBreakdown: MatchBreakdown? = null, // Détails du matching
    
    // Informations du talent (récupérées séparément)
    val fullName: String = "Talent",
    val email: String = "",
    val profileImage: String? = null,
    val location: String? = null,
    val skills: List<String> = emptyList(),
    val description: String? = null
) {
    /**
     * Détails du matching
     */
    data class MatchBreakdown(
        val skillsMatch: Double? = null, // Pourcentage de correspondance des compétences
        val experienceMatch: Double? = null, // Correspondance de l'expérience
        val locationMatch: Double? = null, // Correspondance de la localisation
        val otherFactors: Map<String, Any>? = null // Autres facteurs
    )
    
    /**
     * Couleur du score selon le niveau de matching
     */
    val scoreColor: Long
        get() = when {
            score >= 80 -> 0xFF10B981L // Vert (High Match)
            score >= 60 -> 0xFF3B82F6L // Bleu (Good Match)
            else -> 0xFF6B7280L // Gris (Low Match)
        }
    
    /**
     * Label du score selon le niveau de matching
     */
    val scoreLabel: String
        get() = when {
            score >= 80 -> "High Match"
            score >= 60 -> "Good Match"
            else -> "Match"
        }
}

