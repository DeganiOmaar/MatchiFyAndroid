package com.example.matchify.common

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handler that converts technical backend errors
 * into user-friendly messages
 */
/**
 * Response structure for validation errors from backend
 */
data class ValidationErrorResponse(
    val message: String?,
    val fieldErrors: Map<String, String>?
)

object ErrorHandler {
    
    private const val TAG = "ErrorHandler"
    
    /**
     * Extracts user-friendly error message from exception
     */
    fun getErrorMessage(exception: Throwable, context: ErrorContext = ErrorContext.GENERAL): String {
        return when (exception) {
            is HttpException -> {
                handleHttpException(exception, context)
            }
            is SocketTimeoutException -> {
                "La connexion a expiré. Veuillez réessayer."
            }
            is UnknownHostException, is IOException -> {
                "Problème de connexion. Vérifiez votre connexion internet."
            }
            else -> {
                // Try to extract message from exception
                val message = exception.message ?: ""
                mapTechnicalMessage(message, context)
            }
        }
    }
    
    /**
     * Extracts validation errors from HTTP exception
     */
    fun extractValidationErrors(exception: HttpException): ValidationErrorResponse? {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            errorBody?.let {
                val errorJson = Gson().fromJson(it, JsonObject::class.java)
                val message = errorJson.get("message")?.asString
                val fieldErrors = mutableMapOf<String, String>()
                
                // Try to extract fieldErrors if present
                errorJson.get("fieldErrors")?.asJsonObject?.entrySet()?.forEach { entry ->
                    fieldErrors[entry.key] = entry.value.asString
                }
                
                ValidationErrorResponse(message, fieldErrors.ifEmpty { null })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting validation errors", e)
            null
        }
    }
    
    /**
     * Handles HTTP exceptions and extracts backend messages
     */
    private fun handleHttpException(exception: HttpException, context: ErrorContext): String {
        val code = exception.code()
        val errorBody = exception.response()?.errorBody()?.string()
        
        // Try to extract message from error body
        errorBody?.let {
            try {
                val errorJson = Gson().fromJson(it, JsonObject::class.java)
                val message = errorJson.get("message")?.asString
                    ?: errorJson.get("error")?.asString
                    ?: errorJson.get("msg")?.asString
                
                if (!message.isNullOrBlank()) {
                    return mapTechnicalMessage(message, context)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Could not parse error body: $it")
            }
        }
        
        // Map HTTP status codes to user-friendly messages
        return when (code) {
            400 -> getBadRequestMessage(context)
            401 -> getUnauthorizedMessage(context)
            403 -> "Vous n'avez pas la permission d'effectuer cette action."
            404 -> getNotFoundMessage(context)
            409 -> getConflictMessage(context)
            422 -> "Les données fournies ne sont pas valides. Veuillez vérifier vos informations."
            500, 502, 503 -> "Le serveur rencontre un problème. Veuillez réessayer plus tard."
            else -> "Une erreur s'est produite. Veuillez réessayer."
        }
    }
    
    /**
     * Maps technical error messages to user-friendly ones
     */
    private fun mapTechnicalMessage(message: String, context: ErrorContext): String {
        val lowerMessage = message.lowercase()
        
        // Authentication errors
        if (lowerMessage.contains("unauthorized") || lowerMessage.contains("invalid credentials")) {
            return getUnauthorizedMessage(context)
        }
        
        if (lowerMessage.contains("email") && lowerMessage.contains("password")) {
            return "Email ou mot de passe incorrect."
        }
        
        if (lowerMessage.contains("password") && (lowerMessage.contains("incorrect") || lowerMessage.contains("wrong"))) {
            return "Mot de passe incorrect."
        }
        
        if (lowerMessage.contains("email") && (lowerMessage.contains("not found") || lowerMessage.contains("doesn't exist"))) {
            return "Aucun compte trouvé avec cet email."
        }
        
        // Validation errors
        if (lowerMessage.contains("required") || lowerMessage.contains("missing")) {
            return "Veuillez remplir tous les champs requis."
        }
        
        if (lowerMessage.contains("email") && lowerMessage.contains("invalid")) {
            return "Format d'email invalide."
        }
        
        if (lowerMessage.contains("password") && lowerMessage.contains("weak")) {
            return "Le mot de passe est trop faible. Utilisez au moins 6 caractères."
        }
        
        if (lowerMessage.contains("already exists") || lowerMessage.contains("already registered")) {
            return getConflictMessage(context)
        }
        
        // Network errors
        if (lowerMessage.contains("network") || lowerMessage.contains("connection")) {
            return "Problème de connexion. Vérifiez votre connexion internet."
        }
        
        if (lowerMessage.contains("timeout")) {
            return "La connexion a expiré. Veuillez réessayer."
        }
        
        // If message seems user-friendly already, return it
        if (message.length < 100 && !message.contains("http") && !message.contains("exception")) {
            return message
        }
        
        // Default fallback
        return "Une erreur s'est produite. Veuillez réessayer."
    }
    
    private fun getBadRequestMessage(context: ErrorContext): String {
        return when (context) {
            ErrorContext.LOGIN -> "Veuillez remplir tous les champs."
            ErrorContext.SIGNUP -> "Veuillez remplir tous les champs requis."
            ErrorContext.PROFILE_UPDATE -> "Les informations fournies ne sont pas valides."
            ErrorContext.MISSION_CREATE -> "Veuillez remplir tous les champs de la mission."
            ErrorContext.MISSION_UPDATE -> "Les informations de la mission ne sont pas valides."
            ErrorContext.MISSION_DELETE -> "Impossible de supprimer la mission."
            ErrorContext.PASSWORD_RESET -> "Les informations fournies ne sont pas valides."
            else -> "Les données fournies ne sont pas valides."
        }
    }
    
    private fun getUnauthorizedMessage(context: ErrorContext): String {
        return when (context) {
            ErrorContext.LOGIN -> "Email ou mot de passe incorrect."
            ErrorContext.SIGNUP -> "Erreur d'authentification. Veuillez réessayer."
            ErrorContext.PROFILE_UPDATE -> "Votre session a expiré. Veuillez vous reconnecter."
            ErrorContext.MISSION_CREATE,
            ErrorContext.MISSION_UPDATE,
            ErrorContext.MISSION_DELETE -> "Vous devez être connecté pour effectuer cette action."
            ErrorContext.PASSWORD_RESET -> "Le code de vérification est incorrect ou a expiré."
            else -> "Vous n'êtes pas autorisé à effectuer cette action."
        }
    }
    
    private fun getNotFoundMessage(context: ErrorContext): String {
        return when (context) {
            ErrorContext.PROFILE_UPDATE -> "Profil introuvable."
            ErrorContext.MISSION_UPDATE -> "Mission introuvable."
            ErrorContext.MISSION_DELETE -> "Mission introuvable."
            ErrorContext.PASSWORD_RESET -> "Code de vérification introuvable."
            else -> "Ressource introuvable."
        }
    }
    
    private fun getConflictMessage(context: ErrorContext): String {
        return when (context) {
            ErrorContext.SIGNUP -> "Un compte existe déjà avec cet email."
            ErrorContext.PROFILE_UPDATE -> "Cet email est déjà utilisé par un autre compte."
            else -> "Cette ressource existe déjà."
        }
    }
}

/**
 * Context for error messages to provide more specific messages
 */
enum class ErrorContext {
    GENERAL,
    LOGIN,
    SIGNUP,
    PROFILE_UPDATE,
    MISSION_CREATE,
    MISSION_UPDATE,
    MISSION_DELETE,
    PASSWORD_RESET,
    FORGOT_PASSWORD,
    VERIFY_CODE,
    CONTRACT_CREATE
}

