package com.example.matchify.domain.model

/**
 * Modèle domaine pour un rating
 */
data class Rating(
    val id: String,
    val talentId: String,
    val recruiterId: String,
    val missionId: String? = null,
    val score: Int, // 1 à 5
    val recommended: Boolean = false, // Recommandation du talent (obligatoire)
    val comment: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * Score formaté pour l'affichage (ex: "4.5/5")
     */
    val scoreDisplay: String
        get() = "$score/5"
    
    /**
     * Couleur du score selon la valeur
     */
    val scoreColor: Long
        get() = when (score) {
            5 -> 0xFF10B981L // Vert (Excellent)
            4 -> 0xFF3B82F6L // Bleu (Très bon)
            3 -> 0xFFF59E0BL // Orange (Bon)
            2 -> 0xFFEF4444L // Rouge (Médiocre)
            1 -> 0xFFDC2626L // Rouge foncé (Mauvais)
            else -> 0xFF6B7280L // Gris
        }
}

