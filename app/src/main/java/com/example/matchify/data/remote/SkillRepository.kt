package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.skill.toDomain
import com.example.matchify.domain.model.Skill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SkillRepository(
    private val api: SkillApi
) {
    
    suspend fun searchSkills(query: String): List<Skill> = withContext(Dispatchers.IO) {
        api.searchSkills(query).map { it.toDomain() }
    }
    
    suspend fun getSkillsByIds(ids: List<String>): List<Skill> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext emptyList()
        val idsString = ids.joinToString(",")
        api.getSkillsByIds(idsString).map { it.toDomain() }
    }
}

