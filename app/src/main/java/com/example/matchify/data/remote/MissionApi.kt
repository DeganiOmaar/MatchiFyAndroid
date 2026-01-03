package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.mission.BestMatchMissionsResponseDto
import com.example.matchify.data.remote.dto.mission.CreateMissionRequest
import com.example.matchify.data.remote.dto.mission.MissionDto
import com.example.matchify.data.remote.dto.mission.UpdateMissionRequest
import retrofit2.http.*

interface MissionApi {
    
    @GET("missions/all")
    suspend fun getAllMissions(): List<MissionDto>
    
    @GET("missions")
    suspend fun getMissionsByRecruiter(): List<MissionDto>
    
    @GET("missions/{id}")
    suspend fun getMissionById(@Path("id") id: String): MissionDto
    
    @POST("missions")
    suspend fun createMission(@Body request: CreateMissionRequest): MissionDto
    
    @PUT("missions/{id}")
    suspend fun updateMission(
        @Path("id") id: String,
        @Body request: UpdateMissionRequest
    ): MissionDto
    
    @DELETE("missions/{id}")
    suspend fun deleteMission(@Path("id") id: String): MissionDto
    
    // Best Match Missions avec AI
    // GET /missions/best-match
    // Même endpoint que iOS
    @GET("missions/best-match")
    suspend fun getBestMatchMissions(): BestMatchMissionsResponseDto

    @POST("missions/{id}/complete")
    suspend fun markAsCompleted(@Path("id") id: String): MissionDto

    @POST("missions/{id}/approve-completion")
    suspend fun approveCompletion(
        @Path("id") id: String,
        @Body body: Map<String, String?>
    ): com.example.matchify.data.remote.dto.mission.ApproveCompletionResponseDto
}


