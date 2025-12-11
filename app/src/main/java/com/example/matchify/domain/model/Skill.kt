package com.example.matchify.domain.model

import java.util.*

data class Skill(
    val id: String? = null,
    val _id: String? = null,
    val name: String,
    val source: String, // "ESCO" or "USER"
    val createdBy: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val uniqueId: String
        get() {
            if (!_id.isNullOrBlank()) return _id
            if (!id.isNullOrBlank()) return id
            return "custom-${name.lowercase().replace(" ", "-")}"
        }
    
    val isEsco: Boolean
        get() = source == "ESCO"
    
    val isUserCreated: Boolean
        get() = source == "USER"
    
    fun skillId(): String = uniqueId
}

