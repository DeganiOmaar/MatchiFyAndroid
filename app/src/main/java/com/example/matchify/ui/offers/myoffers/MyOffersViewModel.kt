package com.example.matchify.ui.offers.myoffers

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.OfferRepository
import com.example.matchify.domain.model.Offer
import kotlinx.coroutines.launch

class MyOffersViewModel(
    private val repository: OfferRepository
) : ViewModel() {
    
    var offers by mutableStateOf<List<Offer>>(emptyList())
        private set
    
    var filteredOffers by mutableStateOf<List<Offer>>(emptyList())
        private set
    
    var searchText by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
        private set
    
    var error by mutableStateOf<String?>(null)
        private set
    
    init {
        loadMyOffers()
    }
    
    fun loadMyOffers() {
        viewModelScope.launch {
            isLoading = true
            error = null
            
            try {
                offers = repository.getOffers()
                applyFilters()
                isLoading = false
            } catch (e: Exception) {
                Log.e("MyOffersViewModel", "Error loading offers", e)
                error = e.message ?: "Failed to load offers"
                isLoading = false
            }
        }
    }
    
    fun onSearchTextChange(text: String) {
        searchText = text
        applyFilters()
    }
    
    private fun applyFilters() {
        filteredOffers = if (searchText.isBlank()) {
            offers
        } else {
            offers.filter { offer ->
                offer.title.contains(searchText, ignoreCase = true) ||
                offer.description.contains(searchText, ignoreCase = true) ||
                offer.keywords.any { it.contains(searchText, ignoreCase = true) }
            }
        }
    }
    
    fun deleteOffer(offer: Offer) {
        viewModelScope.launch {
            try {
                repository.deleteOffer(offer.id)
                offers = offers.filter { it.id != offer.id }
                applyFilters()
            } catch (e: Exception) {
                Log.e("MyOffersViewModel", "Error deleting offer", e)
                error = e.message ?: "Failed to delete offer"
            }
        }
    }
    
    fun clearError() {
        error = null
    }
}
