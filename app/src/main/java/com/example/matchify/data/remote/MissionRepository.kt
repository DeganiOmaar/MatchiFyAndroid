package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.CreateMissionRequest
import com.example.matchify.data.remote.dto.MissionDto
import com.example.matchify.data.remote.dto.toDomain
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
    
    suspend fun updateMission(id: String, request: com.example.matchify.data.remote.dto.UpdateMissionRequest): Mission = withContext(Dispatchers.IO) {
        api.updateMission(id, request).toDomain()
    }
    
    suspend fun deleteMission(id: String): Mission = withContext(Dispatchers.IO) {
        api.deleteMission(id).toDomain()
    }
    
    fun isMissionOwner(mission: Mission): Boolean {
        val currentUser = prefs.currentUser.value
        val currentUserId = currentUser?.id
        return mission.recruiterId == currentUserId
    }
}

