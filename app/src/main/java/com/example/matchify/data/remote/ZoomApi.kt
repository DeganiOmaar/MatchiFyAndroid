package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.zoom.ZoomConnectResponse
import com.example.matchify.data.remote.dto.zoom.ZoomStatusResponse
import retrofit2.http.GET

interface ZoomApi {
    /**
     * Obtenir l'URL de connexion Zoom
     * GET /zoom/connect
     * Rôle: recruteur
     */
    @GET("zoom/connect")
    suspend fun getZoomConnectUrl(): ZoomConnectResponse
    
    /**
     * Vérifier le statut de connexion Zoom
     * GET /zoom/status
     * Rôle: recruteur
     */
    @GET("zoom/status")
    suspend fun getZoomStatus(): ZoomStatusResponse
}

