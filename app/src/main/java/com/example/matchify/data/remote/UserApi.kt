package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.GetUserResponseDto
import com.example.matchify.data.remote.dto.profile.TalentUserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    
    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: String): GetUserResponseDto
    
    /**
     * Récupérer tous les talents enregistrés
     * GET /users/talents?limit=&page=
     */
    @GET("users/talents")
    suspend fun getAllTalents(
        @Query("limit") limit: Int? = null,
        @Query("page") page: Int? = null
    ): List<TalentUserDto>
}

