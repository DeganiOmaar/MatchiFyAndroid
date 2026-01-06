package com.example.matchify.ui.interviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InterviewsViewModelFactory(
    private val repository: InterviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InterviewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InterviewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
