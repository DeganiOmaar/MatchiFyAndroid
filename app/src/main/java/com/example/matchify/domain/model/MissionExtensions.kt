package com.example.matchify.domain.model

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to format time matching screenshot exactly: "2h ago", "8h ago", "Yesterday"
 */
val Mission.timePostedText: String
    get() {
        if (createdAt == null) return "Recently"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)
            
            if (date == null) return "Recently"
            
            val now = Date()
            val calendar = Calendar.getInstance()
            calendar.time = now
            
            // Check if it's yesterday
            val yesterday = Calendar.getInstance()
            yesterday.time = now
            yesterday.add(Calendar.DAY_OF_YEAR, -1)
            
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date
            
            if (dateCalendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                dateCalendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday"
            }
            
            val timeInterval = now.time - date.time
            
            val minutes = (timeInterval / (1000 * 60)).toInt()
            val hours = (timeInterval / (1000 * 60 * 60)).toInt()
            val days = (timeInterval / (1000 * 60 * 60 * 24)).toInt()
            
            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> {
                    val outputFormat = SimpleDateFormat("MMM d", Locale.US)
                    outputFormat.format(date)
                }
            }
        } catch (e: Exception) {
            "Recently"
        }
    }

