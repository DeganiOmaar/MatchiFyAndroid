package com.example.matchify.domain.model

/**
 * Modèle domaine pour un talent avec score de matching IA
 */
data class TalentMatch(
    val talentId: String,
    val fullName: String,
    val email: String,
    val profileImage: String?,
    val location: String?,
    val skills: List<String>,
    val talent: List<String>, // Catégories de talent
    val description: String?,
    val matchScore: Int, // Score de 0 à 100
    val reasoning: String?, // Explication du score par l'IA
    val cvUrl: String?
) {
    /**
     * URL complète de l'image de profil
     */
    val profileImageUrl: String?
        get() {
            val path = profileImage?.trim()
            if (path.isNullOrBlank()) return null
            
            val normalized = if (path.startsWith("/")) path else "/$path"
            return "http://10.0.2.2:3000$normalized"
        }
    
    /**
     * URL complète du CV
     */
    val cvUrlFull: String?
        get() {
            val path = cvUrl?.trim()
            if (path.isNullOrBlank()) return null
            
            val normalized = if (path.startsWith("/")) path else "/$path"
            return "http://10.0.2.2:3000$normalized"
        }
    
    /**
     * Couleur du score selon le niveau de matching
     */
    val scoreColor: Long
        get() = when {
            matchScore >= 80 -> 0xFF10B981L // Vert (High Match)
            matchScore >= 60 -> 0xFF3B82F6L // Bleu (Good Match)
            else -> 0xFF6B7280L // Gris (Low Match)
        }
    
    /**
     * Label du score selon le niveau de matching
     */
    val scoreLabel: String
        get() = when {
            matchScore >= 80 -> "High Match"
            matchScore >= 60 -> "Good Match"
            else -> "Match"
        }
}

