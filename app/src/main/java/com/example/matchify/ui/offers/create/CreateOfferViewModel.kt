package com.example.matchify.ui.offers.create

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.OfferRepository
import com.example.matchify.domain.model.OfferCategory
import kotlinx.coroutines.launch
import java.io.File

class CreateOfferViewModel(
    private val repository: OfferRepository,
    initialCategory: OfferCategory
) : ViewModel() {
    
    var category by mutableStateOf<OfferCategory?>(initialCategory)
    
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var price by mutableStateOf("")
    var keywordInput by mutableStateOf("")
    var keywords by mutableStateOf<List<String>>(emptyList())
        private set
    var capabilityInput by mutableStateOf("")
    var capabilities by mutableStateOf<List<String>>(emptyList())
        private set
    
    var bannerImageUri by mutableStateOf<Uri?>(null)
    var galleryImageUris by mutableStateOf<List<Uri>>(emptyList())
    var videoUri by mutableStateOf<Uri?>(null)
    
    var isLoading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var success by mutableStateOf(false)
        private set
    
    fun addKeyword() {
        val trimmed = keywordInput.trim()
        if (trimmed.isNotEmpty() && !keywords.contains(trimmed)) {
            keywords = keywords + trimmed
            keywordInput = ""
        }
    }
    
    fun removeKeyword(keyword: String) {
        keywords = keywords - keyword
    }
    
    fun addCapability() {
        val trimmed = capabilityInput.trim()
        if (trimmed.isNotEmpty() && !capabilities.contains(trimmed)) {
            capabilities = capabilities + trimmed
            capabilityInput = ""
        }
    }
    
    fun removeCapability(capability: String) {
        capabilities = capabilities - capability
    }
    
    fun setBannerImage(uri: Uri) {
        bannerImageUri = uri
    }
    
    fun addGalleryImages(uris: List<Uri>) {
        galleryImageUris = (galleryImageUris + uris).take(10) // Max 10 images
    }
    
    fun removeGalleryImage(uri: Uri) {
        galleryImageUris = galleryImageUris.filter { it != uri }
    }
    
    fun setVideo(uri: Uri) {
        videoUri = uri
    }
    
    fun removeVideo() {
        videoUri = null
    }
    
    fun isFormValid(): Boolean {
        return title.isNotBlank() &&
                keywords.isNotEmpty() &&
                price.isNotBlank() &&
                description.isNotBlank() &&
                bannerImageUri != null
    }
    
    fun createOffer(
        bannerImageFile: File?,
        galleryImageFiles: List<File>,
        videoFile: File?
    ) {
        if (!isFormValid() || bannerImageFile == null) {
            error = "Please fill all required fields"
            return
        }
        
        isLoading = true
        error = null
        
        viewModelScope.launch {
            try {
                val priceInt = price.toIntOrNull() ?: 0
                
                repository.createOffer(
                    category = category?.displayName ?: "",
                    title = title,
                    keywords = keywords,
                    price = priceInt,
                    description = description,
                    bannerImageFile = bannerImageFile,
                    capabilities = capabilities,
                    galleryImageFiles = galleryImageFiles,
                    videoFile = videoFile
                )
                
                success = true
                isLoading = false
            } catch (e: Exception) {
                Log.e("CreateOfferViewModel", "Error creating offer", e)
                error = e.message ?: "Failed to create offer"
                isLoading = false
            }
        }
    }
    
    fun clearError() {
        error = null
    }
}
