package com.example.matchify.ui.offers.browse

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.OfferRepository
import com.example.matchify.domain.model.Offer
import com.example.matchify.domain.model.OfferCategory
import kotlinx.coroutines.launch

class BrowseOffersViewModel(
    private val repository: OfferRepository
) : ViewModel() {
    
    var offers by mutableStateOf<List<Offer>>(emptyList())
        private set
    
    var filteredOffers by mutableStateOf<List<Offer>>(emptyList())
        private set
    
    var searchText by mutableStateOf("")
    
    var selectedCategory by mutableStateOf<OfferCategory?>(null)
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var error by mutableStateOf<String?>(null)
        private set
    
    init {
        loadOffers()
    }
    
    fun loadOffers() {
        viewModelScope.launch {
            isLoading = true
            error = null
            
            try {
                offers = repository.getOffers()
                applyFilters()
                isLoading = false
            } catch (e: Exception) {
                Log.e("BrowseOffersViewModel", "Error loading offers", e)
                error = e.message ?: "Failed to load offers"
                isLoading = false
            }
        }
    }
    
    fun onSearchTextChange(text: String) {
        searchText = text
        applyFilters()
    }
    
    fun onCategorySelected(category: OfferCategory?) {
        selectedCategory = category
        applyFilters()
    }
    
    private fun applyFilters() {
        var result = offers
        
        // Filter by category
        if (selectedCategory != null) {
            result = result.filter { it.category == selectedCategory!!.displayName }
        }
        
        // Filter by search text
        if (searchText.isNotBlank()) {
            result = result.filter { offer ->
                offer.title.contains(searchText, ignoreCase = true) ||
                offer.description.contains(searchText, ignoreCase = true) ||
                offer.keywords.any { it.contains(searchText, ignoreCase = true) }
            }
        }
        
        filteredOffers = result
    }
    
    fun clearError() {
        error = null
    }
}
