package com.example.matchify.ui.portfolio.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.Skill
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditProjectViewModel(
    private val repository: PortfolioRepository,
    private val skillRepository: SkillRepository,
    private val contentResolver: android.content.ContentResolver,
    private val project: Project? = null
) : ViewModel() {
    
    val projectId: String? = project?.projectId
    
    private val _title = MutableStateFlow(project?.title ?: "")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _role = MutableStateFlow(project?.role ?: "")
    val role: StateFlow<String> = _role.asStateFlow()
    
    private val _description = MutableStateFlow(project?.description ?: "")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _projectLink = MutableStateFlow(project?.projectLink ?: "")
    val projectLink: StateFlow<String> = _projectLink.asStateFlow()
    
    private val _selectedSkills = MutableStateFlow<MutableList<Skill>>(mutableListOf())
    val selectedSkills: StateFlow<List<Skill>> = _selectedSkills.asStateFlow()
    
    private val _attachedMedia = MutableStateFlow<MutableList<AttachedMediaItem>>(mutableListOf())
    val attachedMedia: StateFlow<List<AttachedMediaItem>> = _attachedMedia.asStateFlow()
    
    private val _externalLinkInput = MutableStateFlow("")
    val externalLinkInput: StateFlow<String> = _externalLinkInput.asStateFlow()
    
    private val _externalLinkTitle = MutableStateFlow("")
    val externalLinkTitle: StateFlow<String> = _externalLinkTitle.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()
    
    private val _isLoadingSkills = MutableStateFlow(false)
    val isLoadingSkills: StateFlow<Boolean> = _isLoadingSkills.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    init {
        if (project != null) {
            // Load existing media items
            _attachedMedia.value = project.media.map { AttachedMediaItem.ExistingMedia(it) }.toMutableList()
            
            // Load skills by IDs if available
            if (project.skills.isNotEmpty()) {
                loadSkillsByIds(project.skills)
            }
        }
    }
    
    private fun loadSkillsByIds(ids: List<String>) {
        if (ids.isEmpty()) return
        
        _isLoadingSkills.value = true
        viewModelScope.launch {
            try {
                val skills = skillRepository.getSkillsByIds(ids)
                _selectedSkills.value = skills.toMutableList()
            } catch (e: Exception) {
                android.util.Log.e("AddEditProjectViewModel", "Error loading skills: ${e.message}", e)
                // If skills loading fails, use skill names directly
                val skillNames = ids.map { Skill(name = it, source = "USER") }
                _selectedSkills.value = skillNames.toMutableList()
            } finally {
                _isLoadingSkills.value = false
            }
        }
    }
    
    fun setTitle(value: String) {
        _title.value = value
    }
    
    fun setRole(value: String) {
        _role.value = value
    }
    
    fun setDescription(value: String) {
        _description.value = value
    }
    
    fun setProjectLink(value: String) {
        _projectLink.value = value
    }
    
    fun setExternalLinkInput(value: String) {
        _externalLinkInput.value = value
    }
    
    fun setExternalLinkTitle(value: String) {
        _externalLinkTitle.value = value
    }
    
    fun addMedia(media: AttachedMediaItem) {
        val updated = _attachedMedia.value.toMutableList()
        updated.add(media)
        _attachedMedia.value = updated
    }
    
    fun removeMedia(media: AttachedMediaItem) {
        val updated = _attachedMedia.value.toMutableList()
        updated.removeAll { it.id == media.id }
        _attachedMedia.value = updated
    }
    
    fun addExternalLink() {
        val trimmedUrl = _externalLinkInput.value.trim()
        val trimmedTitle = _externalLinkTitle.value.trim()
        
        if (trimmedUrl.isBlank()) {
            _errorMessage.value = "URL cannot be empty"
            return
        }
        
        // Basic URL validation
        try {
            android.net.Uri.parse(trimmedUrl)
        } catch (e: Exception) {
            _errorMessage.value = "Please enter a valid URL"
            return
        }
        
        val link = AttachedMediaItem.ExternalLinkMedia(
            url = trimmedUrl,
            title = trimmedTitle.ifBlank { trimmedUrl }
        )
        addMedia(link)
        _externalLinkInput.value = ""
        _externalLinkTitle.value = ""
        _errorMessage.value = null
    }
    
    fun saveProject() {
        _errorMessage.value = null
        
        // Validation
        if (_title.value.trim().isBlank()) {
            _errorMessage.value = "Le titre est requis."
            return
        }
        
        _isSaving.value = true
        
        viewModelScope.launch {
            try {
                if (projectId != null) {
                    // Update existing project
                    repository.updateProject(
                        id = projectId,
                        title = _title.value.trim(),
                        role = _role.value.trim().takeIf { it.isNotEmpty() },
                        skills = skillNames.takeIf { it.isNotEmpty() },
                        description = _description.value.trim().takeIf { it.isNotEmpty() },
                        projectLink = _projectLink.value.trim().takeIf { it.isNotEmpty() },
                        mediaItems = _attachedMedia.value
                    )
                } else {
                    // Create new project
                    repository.createProject(
                        title = _title.value.trim(),
                        role = _role.value.trim().takeIf { it.isNotEmpty() },
                        skills = skillNames.takeIf { it.isNotEmpty() },
                        description = _description.value.trim().takeIf { it.isNotEmpty() },
                        projectLink = _projectLink.value.trim().takeIf { it.isNotEmpty() },
                        mediaItems = _attachedMedia.value
                    )
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to save project"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    val skillNames: List<String>
        get() = _selectedSkills.value.map { it.name }
    
    fun updateSelectedSkills(skills: List<Skill>) {
        _selectedSkills.value = skills.toMutableList()
    }
}

