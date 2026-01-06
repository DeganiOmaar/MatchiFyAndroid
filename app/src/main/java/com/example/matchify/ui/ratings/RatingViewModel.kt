package com.example.matchify.ui.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.RatingRepository
import com.example.matchify.domain.model.Rating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer les ratings
 */
class RatingViewModel(
    private val repository: RatingRepository
) : ViewModel() {
    
    // État pour le rating du recruteur courant
    private val _myRating = MutableStateFlow<Rating?>(null)
    val myRating: StateFlow<Rating?> = _myRating
    
    // État pour les ratings d'un talent
    private val _talentRatings = MutableStateFlow<TalentRatingsState?>(null)
    val talentRatings: StateFlow<TalentRatingsState?> = _talentRatings
    
    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // État d'erreur
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // État de succès pour la création/mise à jour
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess
    
    /**
     * Charger le rating du recruteur courant pour un talent
     */
    fun loadMyRating(talentId: String, missionId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                _myRating.value = repository.getMyRating(talentId, missionId)
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Charger les ratings d'un talent
     */
    fun loadTalentRatings(talentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = repository.getTalentRatings(talentId)
                _talentRatings.value = TalentRatingsState(
                    talentId = response.talentId,
                    averageScore = response.averageScore,
                    count = response.count,
                    bayesianScore = response.bayesianScore,
                    ratings = response.ratings
                )
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Créer ou mettre à jour un rating
     */
    fun createOrUpdateRating(
        talentId: String,
        missionId: String? = null,
        score: Int,
        recommended: Boolean = false,
        comment: String? = null
    ) {
        // Validation
        if (score < 1 || score > 5) {
            _errorMessage.value = "Le score doit être entre 1 et 5"
            return
        }
        
        if (comment != null && comment.length > 1000) {
            _errorMessage.value = "Le commentaire ne peut pas dépasser 1000 caractères"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _saveSuccess.value = false
            
            try {
                val rating = repository.createOrUpdateRating(
                    talentId = talentId,
                    missionId = missionId,
                    score = score,
                    recommended = recommended,
                    comment = comment
                )
                
                _myRating.value = rating
                _saveSuccess.value = true
                
                // Recharger les ratings du talent pour mettre à jour la moyenne
                loadTalentRatings(talentId)
                
            } catch (e: Exception) {
                // Logger l'erreur complète pour déboguer
                android.util.Log.e("RatingViewModel", "Erreur lors de la création du rating", e)
                
                // Si c'est une HttpException, essayer d'extraire le message du backend
                val errorMsg = if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    android.util.Log.e("RatingViewModel", "HTTP ${e.code()} Error: $errorBody")
                    
                    // Essayer d'extraire le message du backend
                    errorBody?.let {
                        try {
                            val errorJson = com.google.gson.Gson().fromJson(it, com.google.gson.JsonObject::class.java)
                            errorJson.get("message")?.asString
                                ?: errorJson.get("error")?.asString
                                ?: ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                        } catch (parseException: Exception) {
                            ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                        }
                    } ?: ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                } else {
                    // Pour les autres exceptions, utiliser le handler standard
                    ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                }
                
                // Ne pas afficher "Problème de connexion" pour les erreurs HTTP 400/422 (validation)
                _errorMessage.value = if (e is retrofit2.HttpException && (e.code() == 400 || e.code() == 422)) {
                    errorMsg
                } else if (errorMsg.contains("Problème de connexion") && e is retrofit2.HttpException) {
                    // Si c'est une erreur HTTP mais qu'on a "Problème de connexion", utiliser un message plus précis
                    when (e.code()) {
                        400 -> "Les données fournies ne sont pas valides. Veuillez vérifier vos informations."
                        422 -> "Les données fournies ne sont pas valides. Veuillez vérifier vos informations."
                        401 -> "Votre session a expiré. Veuillez vous reconnecter."
                        403 -> "Vous n'avez pas la permission d'effectuer cette action."
                        500, 502, 503 -> "Le serveur rencontre un problème. Veuillez réessayer plus tard."
                        else -> errorMsg
                    }
                } else {
                    errorMsg
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Supprimer un rating
     */
    fun deleteRating(ratingId: String, talentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // Log pour debug
                android.util.Log.d("RatingViewModel", "Deleting rating with ID: $ratingId")
                
                repository.deleteRating(ratingId)
                
                android.util.Log.d("RatingViewModel", "Rating deleted successfully")
                
                // Si c'était le rating du recruteur courant, le réinitialiser
                if (_myRating.value?.id == ratingId) {
                    _myRating.value = null
                }
                
                // Recharger les ratings du talent pour mettre à jour la moyenne
                loadTalentRatings(talentId)
                
                // Recharger aussi myRating au cas où
                loadMyRating(talentId, null)
                
            } catch (e: Exception) {
                android.util.Log.e("RatingViewModel", "Error deleting rating: ${e.message}", e)
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Réinitialiser l'état
     */
    fun reset() {
        _myRating.value = null
        _talentRatings.value = null
        _errorMessage.value = null
        _saveSuccess.value = false
    }
}

/**
 * État pour les ratings d'un talent
 */
data class TalentRatingsState(
    val talentId: String,
    val averageScore: Double?,
    val count: Int,
    val bayesianScore: Double?, // score bayésien pondéré
    val ratings: List<Rating>
)

