package com.example.matchify.domain.model

/**
 * Modèle domaine pour un talent scoré par l'IA pour une mission
 * Correspond à TalentScoredDto du backend
 */
data class TalentScored(
    val talentId: String,
    val fullName: String,
    val score: Int, // 0-100, score global
    val skillMatch: Double, // 0-1, similarité de compétences
    val experienceMatch: Double, // 0-1, adéquation expérience
    val matchingSkills: List<String>, // compétences de la mission retrouvées chez le talent
    val missionSkills: List<String> // compétences requises par la mission
) {
    /**
     * Score en pourcentage formaté (ex: "87%")
     */
    val scorePercentage: String
        get() = "$score%"
    
    /**
     * SkillMatch en pourcentage formaté (ex: "85%")
     */
    val skillMatchPercentage: String
        get() = "${(skillMatch * 100).toInt()}%"
    
    /**
     * ExperienceMatch en pourcentage formaté (ex: "90%")
     */
    val experienceMatchPercentage: String
        get() = "${(experienceMatch * 100).toInt()}%"
    
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
            score >= 80 -> "Excellent match"
            score >= 60 -> "Bon match"
            else -> "Match acceptable"
        }
}

