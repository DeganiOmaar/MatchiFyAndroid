package com.example.matchify.ui.talent.filtering.test

import com.example.matchify.domain.model.TalentCandidate

/**
 * Utilitaires pour valider le modèle IA
 */
object ModelValidationUtils {
    
    /**
     * Valider que les scores sont cohérents
     */
    fun validateScores(candidates: List<TalentCandidate>): ValidationReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        // Vérifier que tous les scores sont dans la plage valide
        candidates.forEach { candidate ->
            if (candidate.score < 0 || candidate.score > 100) {
                errors.add("Score invalide pour ${candidate.fullName}: ${candidate.score} (doit être entre 0 et 100)")
            }
        }
        
        // Vérifier la distribution des scores
        if (candidates.size > 5) {
            val highScores = candidates.count { it.score >= 80 }
            val lowScores = candidates.count { it.score < 60 }
            
            if (highScores == 0) {
                warnings.add("Aucun score élevé (≥80%) trouvé pour ${candidates.size} candidats")
            }
            
            if (lowScores > candidates.size * 0.8) {
                warnings.add("Trop de scores faibles (<60%): $lowScores/${candidates.size}")
            }
        }
        
        return ValidationReport(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Valider la cohérence du breakdown avec le score global
     */
    fun validateBreakdownConsistency(candidates: List<TalentCandidate>): ValidationReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        candidates.forEach { candidate ->
            candidate.matchBreakdown?.let { breakdown ->
                val breakdownScores = listOfNotNull(
                    breakdown.skillsMatch,
                    breakdown.experienceMatch,
                    breakdown.locationMatch
                )
                
                if (breakdownScores.isEmpty()) {
                    warnings.add("Breakdown vide pour ${candidate.fullName}")
                    return@let
                }
                
                val avgBreakdown = breakdownScores.average()
                val difference = kotlin.math.abs(candidate.score - avgBreakdown)
                
                if (difference > 20) {
                    errors.add(
                        "Incohérence importante pour ${candidate.fullName}: " +
                        "score global=${candidate.score}%, moyenne breakdown=${avgBreakdown.toInt()}%, " +
                        "différence=${difference.toInt()}%"
                    )
                } else if (difference > 10) {
                    warnings.add(
                        "Petite incohérence pour ${candidate.fullName}: " +
                        "différence=${difference.toInt()}%"
                    )
                }
                
                // Vérifier que les scores du breakdown sont dans la plage valide
                breakdownScores.forEach { score ->
                    if (score < 0 || score > 100) {
                        errors.add("Score de breakdown invalide pour ${candidate.fullName}: $score")
                    }
                }
            }
        }
        
        return ValidationReport(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Valider que les raisons sont présentes et pertinentes
     */
    fun validateReasons(candidates: List<TalentCandidate>): ValidationReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        
        candidates.forEach { candidate ->
            if (candidate.score >= 70 && candidate.reasons.isNullOrBlank()) {
                warnings.add("Score élevé (${candidate.score}%) sans raisons pour ${candidate.fullName}")
            }
            
            candidate.reasons?.let { reasons ->
                if (reasons.length < 20) {
                    warnings.add("Raisons trop courtes pour ${candidate.fullName}: ${reasons.length} caractères")
                }
            }
        }
        
        return ValidationReport(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
    
    /**
     * Valider le tri des résultats
     */
    fun validateSorting(candidates: List<TalentCandidate>): ValidationReport {
        val errors = mutableListOf<String>()
        
        if (candidates.size > 1) {
            val isSorted = candidates.zipWithNext().all { (a, b) -> a.score >= b.score }
            
            if (!isSorted) {
                errors.add("Les candidats ne sont pas triés par score décroissant")
            }
        }
        
        return ValidationReport(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = emptyList()
        )
    }
    
    /**
     * Rapport de validation complet
     */
    data class ValidationReport(
        val isValid: Boolean,
        val errors: List<String>,
        val warnings: List<String>
    ) {
        val hasIssues: Boolean
            get() = errors.isNotEmpty() || warnings.isNotEmpty()
    }
    
    /**
     * Générer un rapport de validation complet
     */
    fun generateFullReport(candidates: List<TalentCandidate>): FullValidationReport {
        val scoreValidation = validateScores(candidates)
        val breakdownValidation = validateBreakdownConsistency(candidates)
        val reasonsValidation = validateReasons(candidates)
        val sortingValidation = validateSorting(candidates)
        
        val allErrors = scoreValidation.errors +
                breakdownValidation.errors +
                reasonsValidation.errors +
                sortingValidation.errors
        
        val allWarnings = scoreValidation.warnings +
                breakdownValidation.warnings +
                reasonsValidation.warnings +
                sortingValidation.warnings
        
        return FullValidationReport(
            isValid = allErrors.isEmpty(),
            errors = allErrors,
            warnings = allWarnings,
            candidatesCount = candidates.size,
            averageScore = if (candidates.isNotEmpty()) {
                candidates.map { it.score }.average()
            } else {
                0.0
            }
        )
    }
    
    /**
     * Rapport de validation complet
     */
    data class FullValidationReport(
        val isValid: Boolean,
        val errors: List<String>,
        val warnings: List<String>,
        val candidatesCount: Int,
        val averageScore: Double
    )
}

