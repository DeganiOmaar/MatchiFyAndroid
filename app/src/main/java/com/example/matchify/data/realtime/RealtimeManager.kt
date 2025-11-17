package com.example.matchify.data.realtime

import android.util.Log
import com.example.matchify.data.local.AuthPreferences

class RealtimeManager(
    private val authPreferences: AuthPreferences,
    private val baseUrl: String = "http://10.0.2.2:3000"
) {
    val missionClient: MissionRealtimeClient by lazy {
        MissionRealtimeClient(authPreferences, baseUrl)
    }
    
    val profileClient: ProfileRealtimeClient by lazy {
        ProfileRealtimeClient(authPreferences, baseUrl)
    }
    
    fun connectAll() {
        Log.d("RealtimeManager", "Connecting all realtime clients...")
        missionClient.connect()
        profileClient.connect()
    }
    
    fun disconnectAll() {
        Log.d("RealtimeManager", "Disconnecting all realtime clients...")
        missionClient.disconnect()
        profileClient.disconnect()
    }
    
    companion object {
        @Volatile
        private var INSTANCE: RealtimeManager? = null
        
        fun initialize(authPreferences: AuthPreferences, baseUrl: String = "http://10.0.2.2:3000"): RealtimeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RealtimeManager(authPreferences, baseUrl).also { INSTANCE = it }
            }
        }
        
        fun getInstance(): RealtimeManager {
            return INSTANCE ?: throw IllegalStateException("RealtimeManager has not been initialized")
        }
    }
}

