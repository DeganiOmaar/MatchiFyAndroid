package com.example.matchify.ui.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RatingRepository

class RatingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = RatingRepository(apiService.ratingApi)
            @Suppress("UNCHECKED_CAST")
            return RatingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

