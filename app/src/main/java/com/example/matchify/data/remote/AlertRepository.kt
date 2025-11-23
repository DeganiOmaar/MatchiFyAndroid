package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.alert.toDomain
import com.example.matchify.domain.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertRepository(
    private val api: AlertApi
) {
    
    suspend fun getAlerts(): List<Alert> = withContext(Dispatchers.IO) {
        api.getAlerts().map { it.toDomain() }
    }
    
    suspend fun getUnreadCount(): Int = withContext(Dispatchers.IO) {
        val response = api.getUnreadCount()
        response["count"] as? Int ?: 0
    }
    
    suspend fun markAsRead(alertId: String): Alert = withContext(Dispatchers.IO) {
        api.markAsRead(alertId).toDomain()
    }
    
    suspend fun markAllAsRead(): Unit = withContext(Dispatchers.IO) {
        api.markAllAsRead()
    }
}

