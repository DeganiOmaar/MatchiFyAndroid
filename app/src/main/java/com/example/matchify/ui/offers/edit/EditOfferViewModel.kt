package com.example.matchify.ui.offers.edit

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.OfferRepository
import com.example.matchify.domain.model.Offer
import com.example.matchify.domain.model.UpdateOfferRequest
import kotlinx.coroutines.launch
import java.io.File

class EditOfferViewModel(
    private val repository: OfferRepository,
    private val offer: Offer
) : ViewModel() {
    
    var title by mutableStateOf(offer.title)
    var description by mutableStateOf(offer.description)
    var price by mutableStateOf(offer.price.toString())
    var keywordInput by mutableStateOf("")
    var keywords by mutableStateOf(offer.keywords)
        private set
    var capabilityInput by mutableStateOf("")
    var capabilities by mutableStateOf(offer.capabilities ?: emptyList())
        private set
    
    // New Media Selection State
    var newBannerUri by mutableStateOf<Uri?>(null)
    var newGalleryUris by mutableStateOf<List<Uri>>(emptyList())
    var newVideoUri by mutableStateOf<Uri?>(null)
    
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
        keywords = keywords.filter { it != keyword }
    }
    
    fun addCapability() {
        val trimmed = capabilityInput.trim()
        if (trimmed.isNotEmpty() && !capabilities.contains(trimmed)) {
            capabilities = capabilities + trimmed
            capabilityInput = ""
        }
    }
    
    fun removeCapability(capability: String) {
        capabilities = capabilities.filter { it != capability }
    }
    
    fun setNewBanner(uri: Uri) {
        newBannerUri = uri
    }
    
    fun addNewGalleryImages(uris: List<Uri>) {
        newGalleryUris = (newGalleryUris + uris).take(10)
    }
    
    fun removeNewGalleryImage(uri: Uri) {
        newGalleryUris = newGalleryUris.filter { it != uri }
    }
    
    fun setNewVideo(uri: Uri) {
        newVideoUri = uri
    }
    
    fun removeNewVideo() {
        newVideoUri = null
    }

    // Note: We don't remove existing images in the UI state for simplicity, 
    // as the backend logic (Replace All) makes partial deletion complex without re-uploading.
    // The user effectively "Replaces" the gallery if they add any new images.
    
    fun isFormValid(): Boolean {
        return title.isNotBlank() &&
                description.isNotBlank() &&
                price.isNotBlank() &&
                price.toDoubleOrNull() != null &&
                keywords.isNotEmpty()
    }
    
    fun updateOffer(
        bannerImageFile: File?,
        galleryImageFiles: List<File>?,
        videoFile: File?,
        onSuccess: () -> Unit
    ) {
        if (!isFormValid()) {
            error = "Please fill all required fields"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            error = null
            
            try {
                val priceInt = price.toIntOrNull() ?: 0
                
                Log.d("EditOfferViewModel", "Updating offer: title=$title, price=$priceInt")
                
                repository.updateOffer(
                    id = offer.id,
                    title = title,
                    description = description,
                    price = priceInt,
                    keywords = keywords,
                    capabilities = capabilities.takeIf { it.isNotEmpty() },
                    bannerImageFile = bannerImageFile,
                    galleryImageFiles = galleryImageFiles,
                    videoFile = videoFile,
                    // We don't update category
                )
                
                success = true
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                Log.e("EditOfferViewModel", "Error updating offer", e)
                error = e.message ?: "Failed to update offer"
                isLoading = false
            }
        }
    }
    
    fun clearError() {
        error = null
    }
}
