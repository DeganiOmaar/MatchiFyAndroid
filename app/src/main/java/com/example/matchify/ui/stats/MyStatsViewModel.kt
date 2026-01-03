package com.example.matchify.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.ApiService
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

class MyStatsViewModel(
    private val apiService: ApiService = ApiService.getInstance()
) : ViewModel() {
    
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
                // Map timeframe to days
                val days = mapTimeframeToDays(_selectedTimeframe.value)
                
                // Fetch stats from API
                val statsResponse = apiService.talentApi.getTalentStats(days)
                
                // Update stats model, preserving existing earnings and job success score
                _stats.value = Stats(
                    twelveMonthEarnings = statsResponse.totalEarnings,
                    jobSuccessScore = _stats.value?.jobSuccessScore,
                    proposalsSent = statsResponse.totalProposalsSent,
                    proposalsAccepted = statsResponse.totalProposalsAccepted,
                    proposalsRefused = statsResponse.totalProposalsRefused
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
    
    private fun mapTimeframeToDays(timeframe: StatsTimeframe): Int {
        return when (timeframe) {
            StatsTimeframe.LAST_7_DAYS -> 7
            StatsTimeframe.LAST_30_DAYS -> 30
            StatsTimeframe.LAST_90_DAYS -> 90
            StatsTimeframe.LAST_12_MONTHS -> 365
        }
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
            return MyStatsViewModel(ApiService.getInstance()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

