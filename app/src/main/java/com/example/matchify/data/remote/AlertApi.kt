package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.alert.AlertDto
import retrofit2.http.*

interface AlertApi {
    
    @GET("alerts")
    suspend fun getAlerts(): List<AlertDto>
    
    @GET("alerts/unread/count")
    suspend fun getUnreadCount(): Map<String, Int>
    
    @PATCH("alerts/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): AlertDto
    
    @PATCH("alerts/read-all")
    suspend fun markAllAsRead(): Map<String, String>
}

