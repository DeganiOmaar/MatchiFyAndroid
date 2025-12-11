package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.alert.AlertDto
import retrofit2.http.*

interface AlertApi {
    
    @GET("alerts")
    suspend fun getAlerts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): AlertsResponseDto
    
    @GET("alerts/unread-count")
    suspend fun getUnreadCount(): UnreadCountResponseDto
    
    @GET("alerts/{id}")
    suspend fun getAlertById(@Path("id") id: String): AlertDto
    
    @PATCH("alerts/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): AlertDto
    
    @PATCH("alerts/read-all")
    suspend fun markAllAsRead(): MarkAllReadResponseDto
}

data class AlertsResponseDto(
    val alerts: List<AlertDto>,
    val total: Int,
    val page: Int,
    val limit: Int
)

data class UnreadCountResponseDto(
    val count: Int
)

data class MarkAllReadResponseDto(
    val count: Int
)

