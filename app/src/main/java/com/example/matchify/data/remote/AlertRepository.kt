package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.alert.AlertDto
import com.example.matchify.data.remote.dto.alert.toDomain
import com.example.matchify.domain.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlertRepository(
    private val api: AlertApi
) {
    
    suspend fun getAlerts(page: Int = 1, limit: Int = 50): List<Alert> = withContext(Dispatchers.IO) {
        api.getAlerts(page, limit).alerts.map { it.toDomain() }
    }
    
    suspend fun getUnreadCount(): Int = withContext(Dispatchers.IO) {
        api.getUnreadCount().count
    }
    
    suspend fun getAlertById(id: String): Alert = withContext(Dispatchers.IO) {
        api.getAlertById(id).toDomain()
    }
    
    suspend fun markAsRead(id: String): Alert = withContext(Dispatchers.IO) {
        api.markAsRead(id).toDomain()
    }
    
    suspend fun markAllAsRead(): Int = withContext(Dispatchers.IO) {
        api.markAllAsRead().count
    }
}

