package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.zoom.ZoomConnectResponse
import com.example.matchify.data.remote.dto.zoom.ZoomStatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ZoomRepository(
    private val api: ZoomApi
) {
    /**
     * Obtenir l'URL de connexion Zoom
     */
    suspend fun getZoomConnectUrl(): ZoomConnectResponse = withContext(Dispatchers.IO) {
        api.getZoomConnectUrl()
    }
    
    /**
     * Vérifier le statut de connexion Zoom
     */
    suspend fun getZoomStatus(): ZoomStatusResponse = withContext(Dispatchers.IO) {
        api.getZoomStatus()
    }
}

