package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.mission.CreateMissionRequest
import com.example.matchify.data.remote.dto.mission.MissionDto
import com.example.matchify.data.remote.dto.mission.UpdateMissionRequest
import com.example.matchify.data.remote.dto.mission.toDomain
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MissionRepository(
    private val api: MissionApi,
    private val prefs: AuthPreferences
) {
    
    suspend fun getAllMissions(): List<Mission> = withContext(Dispatchers.IO) {
        api.getAllMissions().map { it.toDomain() }
    }
    
    suspend fun getMissionsByRecruiter(): List<Mission> = withContext(Dispatchers.IO) {
        api.getMissionsByRecruiter().map { it.toDomain() }
    }
    
    suspend fun getMissionById(id: String): Mission = withContext(Dispatchers.IO) {
        api.getMissionById(id).toDomain()
    }
    
    suspend fun createMission(request: CreateMissionRequest): Mission = withContext(Dispatchers.IO) {
        api.createMission(request).toDomain()
    }
    
    suspend fun updateMission(id: String, request: UpdateMissionRequest): Mission = withContext(Dispatchers.IO) {
        api.updateMission(id, request).toDomain()
    }
    
    suspend fun deleteMission(id: String): Mission = withContext(Dispatchers.IO) {
        api.deleteMission(id).toDomain()
    }
    
    fun isMissionOwner(mission: Mission): Boolean {
        val currentUser = prefs.currentUser.value
        val currentUserId = currentUser?.id
        
        // Debug: Log pour vérifier
        android.util.Log.d("MissionRepository", "isMissionOwner - currentUserId: $currentUserId, mission.recruiterId: ${mission.recruiterId}, user: ${currentUser?.fullName}")
        
        if (currentUserId == null || currentUserId.isEmpty()) {
            android.util.Log.d("MissionRepository", "isMissionOwner - currentUserId is null or empty")
            return false
        }
        
        val isOwner = mission.recruiterId == currentUserId
        android.util.Log.d("MissionRepository", "isMissionOwner - result: $isOwner")
        
        return isOwner
    }
}

