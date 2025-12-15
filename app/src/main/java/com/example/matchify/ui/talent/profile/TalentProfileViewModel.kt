package com.example.matchify.ui.talent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.data.remote.dto.profile.toDomain
import com.example.matchify.data.realtime.ProfileRealtimeClient
import com.example.matchify.data.realtime.ProfileRealtimeEvent
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

class TalentProfileViewModel(
    private val prefs: AuthPreferences,
    private val repository: TalentRepository,
    private val portfolioRepository: PortfolioRepository,
    private val skillRepository: SkillRepository,
    private val realtimeClient: ProfileRealtimeClient
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _joinedDate = MutableStateFlow("-")
    val joinedDate: StateFlow<String> = _joinedDate

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects
    
    private val _isLoadingProjects = MutableStateFlow(false)
    val isLoadingProjects: StateFlow<Boolean> = _isLoadingProjects
    
    private val _skillNames = MutableStateFlow<List<String>>(emptyList())
    val skillNames: StateFlow<List<String>> = _skillNames
    
    private val _isUploadingCV = MutableStateFlow(false)
    val isUploadingCV: StateFlow<Boolean> = _isUploadingCV
    
    private val _cvUploadError = MutableStateFlow<String?>(null)
    val cvUploadError: StateFlow<String?> = _cvUploadError

    init {
        // Load user data from local preferences first
        loadUserFromPreferences()
        // Then fetch fresh data from API
        loadProfile()
        // Load projects
        loadProjects()
        // Load skill names
        loadSkillNames()
        // Observe realtime updates
        observeRealtimeUpdates()
    }
    
    private fun observeRealtimeUpdates() {
        realtimeClient.connect()
        viewModelScope.launch {
            realtimeClient.events.collect { event ->
                when (event) {
                    is ProfileRealtimeEvent.ProfileUpdated -> {
                        // Only update if it's the current user's profile
                        val currentUserId = _user.value?.id
                        if (event.user.id == currentUserId) {
                            _user.value = event.user
                            prefs.saveUser(event.user)
                            updateJoinedDate(event.user.createdAt)
                            loadSkillNames() // Reload skill names when profile is updated
                            Log.d("TalentProfileViewModel", "Profile updated via realtime: ${event.user.fullName}")
                        }
                    }
                    is ProfileRealtimeEvent.ProfileDeleted -> {
                        if (event.userId == _user.value?.id) {
                            _errorMessage.value = "Votre profil a été supprimé"
                        }
                    }
                }
            }
        }
    }

    private fun loadUserFromPreferences() {
        viewModelScope.launch {
            try {
                val localUser = prefs.user.first()
                if (localUser != null) {
                    _user.value = localUser
                    updateJoinedDate(localUser.createdAt)
                    loadSkillNames() // Load skill names when user is loaded from preferences
                    Log.d("TalentProfileViewModel", "Loaded user from preferences: ${localUser.fullName}")
                }
            } catch (e: Exception) {
                Log.e("TalentProfileViewModel", "Error loading user from preferences: ${e.message}", e)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val tokenFromStore = prefs.getTokenValue()
                if (tokenFromStore.isNullOrBlank()) {
                    Log.w("TalentProfileViewModel", "Token not available in DataStore, skipping API call")
                    return@launch
                }

                kotlinx.coroutines.delay(100)

                val response = repository.getTalentProfile()
                Log.d("TalentProfileViewModel", "API Response: $response")

                val userDto = response.user
                    ?: throw IllegalStateException("Profil talent manquant dans la réponse de l'API")
                val userData = userDto.toDomain()
                _user.value = userData
                prefs.saveUser(userData)

                updateJoinedDate(userData.createdAt)
                loadSkillNames() // Load skill names after profile is loaded
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("TalentProfileViewModel", "Error loading profile: ${e.message}", e)
                if (_user.value == null) {
                    _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
                }
            }
        }
    }

    private fun updateJoinedDate(dateString: String?) {
        if (dateString == null) {
            _joinedDate.value = "-"
            return
        }

        try {
            val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            val date = isoFormat.parse(dateString)
            
            if (date != null) {
                val outputFormat = java.text.SimpleDateFormat("dd MMMM, yyyy", java.util.Locale.US)
                _joinedDate.value = outputFormat.format(date)
            } else {
                val formatted = dateString.substring(0, 10)
                _joinedDate.value = formatted
            }
        } catch (e: Exception) {
            Log.e("TalentProfileViewModel", "Error formatting date: ${e.message}", e)
            val formatted = if (dateString.length >= 10) dateString.substring(0, 10) else dateString
            _joinedDate.value = formatted
        }
    }
    
    fun refreshProfile() {
        loadProfile()
    }
    
    fun loadProjects() {
        _isLoadingProjects.value = true
        viewModelScope.launch {
            try {
                val projectsList = portfolioRepository.getProjects()
                _projects.value = projectsList
            } catch (e: Exception) {
                Log.e("TalentProfileViewModel", "Error loading projects: ${e.message}", e)
                // Silently fail - projects will remain empty
            } finally {
                _isLoadingProjects.value = false
            }
        }
    }
    
    fun loadSkillNames() {
        val skillIds = _user.value?.skills
        if (skillIds.isNullOrEmpty()) {
            _skillNames.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d("TalentProfileViewModel", "Loading skill names for IDs: $skillIds")
                val skills = skillRepository.getSkillsByIds(skillIds)
                _skillNames.value = skills.map { it.name }
                Log.d("TalentProfileViewModel", "Loaded skill names: ${_skillNames.value}")
            } catch (e: Exception) {
                Log.e("TalentProfileViewModel", "Failed to load skill names: ${e.message}", e)
                Log.e("TalentProfileViewModel", "Exception type: ${e.javaClass.simpleName}")
                Log.e("TalentProfileViewModel", "Stack trace: ${e.stackTraceToString()}")
                // Don't fall back to IDs - just show nothing if it fails
                _skillNames.value = emptyList()
            }
        }
    }
    
    /**
     * Upload CV file
     * Même comportement que iOS
     */
    fun uploadCV() {
        // Cette fonction déclenchera le sélecteur de fichier dans l'écran
        // L'écran appellera uploadCVFile avec le fichier sélectionné
    }
    
    fun uploadCVFile(fileUri: android.net.Uri, context: android.content.Context) {
        _isUploadingCV.value = true
        _cvUploadError.value = null
        
        viewModelScope.launch {
            try {
                // Lire le fichier depuis l'URI
                val inputStream = context.contentResolver.openInputStream(fileUri)
                val file = java.io.File(context.cacheDir, "cv_temp_${System.currentTimeMillis()}")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Déterminer l'extension
                val fileName = context.contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                } ?: "cv.pdf"
                
                // Vérifier le type de fichier
                val extension = fileName.substringAfterLast('.', "").lowercase()
                if (extension !in listOf("pdf", "doc", "docx")) {
                    throw IllegalArgumentException("Format de fichier non supporté. Utilisez PDF, DOC ou DOCX.")
                }
                
                // Renommer le fichier avec la bonne extension
                val renamedFile = java.io.File(file.parent, fileName)
                file.renameTo(renamedFile)
                
                // Upload
                val response = repository.uploadCV(renamedFile)
                val userDto = response.user
                    ?: throw IllegalStateException("Réponse invalide du serveur")
                
                val userData = userDto.toDomain()
                _user.value = userData
                prefs.saveUser(userData)
                
                // Nettoyer le fichier temporaire
                renamedFile.delete()
                
                _isUploadingCV.value = false
                refreshProfile() // Recharger le profil pour afficher le CV
            } catch (e: Exception) {
                Log.e("TalentProfileViewModel", "Error uploading CV: ${e.message}", e)
                _isUploadingCV.value = false
                _cvUploadError.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
            }
        }
    }
}

