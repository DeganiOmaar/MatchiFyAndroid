package com.example.matchify.domain.model

import java.text.NumberFormat
import java.util.*

data class Stats(
    val twelveMonthEarnings: Double = 0.0,
    val jobSuccessScore: Int? = null,
    val proposalsSent: Int = 0,
    val proposalsViewed: Int = 0,
    val interviews: Int = 0,
    val hires: Int = 0
) {
    val hasJobSuccessScore: Boolean
        get() = jobSuccessScore != null
    
    val formattedEarnings: String
        get() {
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            formatter.maximumFractionDigits = 0
            return formatter.format(twelveMonthEarnings)
        }
}

