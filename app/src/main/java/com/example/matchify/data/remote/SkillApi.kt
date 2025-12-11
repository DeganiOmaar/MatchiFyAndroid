package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.skill.SkillDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SkillApi {
    
    @GET("skills")
    suspend fun searchSkills(@Query("query") query: String): List<SkillDto>
    
    @GET("skills/by-ids")
    suspend fun getSkillsByIds(@Query("ids") ids: String): List<SkillDto>
}

