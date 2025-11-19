package com.example.matchify.domain.model

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to format time as "Posted X minutes/hours/days ago"
 */
val Mission.timePostedText: String
    get() {
        if (createdAt == null) return "Posted recently"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)
            
            if (date == null) return "Posted recently"
            
            val now = Date()
            val timeInterval = now.time - date.time
            
            val minutes = (timeInterval / (1000 * 60)).toInt()
            val hours = (timeInterval / (1000 * 60 * 60)).toInt()
            val days = (timeInterval / (1000 * 60 * 60 * 24)).toInt()
            
            when {
                minutes < 1 -> "Posted just now"
                minutes < 60 -> "Posted $minutes minute${if (minutes == 1) "" else "s"} ago"
                hours < 24 -> "Posted $hours hour${if (hours == 1) "" else "s"} ago"
                days < 7 -> "Posted $days day${if (days == 1) "" else "s"} ago"
                else -> {
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    "Posted ${outputFormat.format(date)}"
                }
            }
        } catch (e: Exception) {
            "Posted recently"
        }
    }

