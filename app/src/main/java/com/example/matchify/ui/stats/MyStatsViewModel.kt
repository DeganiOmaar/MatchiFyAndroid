package com.example.matchify.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.domain.model.Stats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class StatsTimeframe {
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_90_DAYS,
    LAST_12_MONTHS
}

class MyStatsViewModel : ViewModel() {
    
    private val _stats = MutableStateFlow<Stats?>(null)
    val stats: StateFlow<Stats?> = _stats.asStateFlow()
    
    private val _selectedTimeframe = MutableStateFlow(StatsTimeframe.LAST_7_DAYS)
    val selectedTimeframe: StateFlow<StatsTimeframe> = _selectedTimeframe.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadStats()
    }
    
    fun loadStats() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                // TODO: Replace with actual API call
                // For now, use mock data
                kotlinx.coroutines.delay(500)
                _stats.value = Stats(
                    twelveMonthEarnings = 0.0,
                    jobSuccessScore = null,
                    proposalsSent = 0,
                    proposalsViewed = 0,
                    interviews = 0,
                    hires = 0
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load stats"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectTimeframe(timeframe: StatsTimeframe) {
        _selectedTimeframe.value = timeframe
        loadStats()
    }
    
    val formattedEarnings: String
        get() = _stats.value?.formattedEarnings ?: "$0"
    
    val jobSuccessScoreText: String
        get() = _stats.value?.jobSuccessScore?.toString() ?: "–"
    
    val hasJobSuccessScore: Boolean
        get() = _stats.value?.hasJobSuccessScore ?: false
    
    val proposalsSentText: String
        get() {
            val count = _stats.value?.proposalsSent ?: 0
            return if (count == 1) "$count proposal sent" else "$count proposals sent"
        }
}

class MyStatsViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyStatsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

