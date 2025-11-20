package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.portfolio.ProjectDto
import com.example.matchify.data.remote.dto.portfolio.ProjectResponseDto
import com.example.matchify.data.remote.dto.portfolio.ProjectsResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PortfolioApi {
    
    @GET("talent/portfolio")
    suspend fun getProjects(): ProjectsResponseDto
    
    @GET("talent/portfolio/{id}")
    suspend fun getProjectById(@Path("id") id: String): ProjectResponseDto
    
    @Multipart
    @POST("talent/portfolio")
    suspend fun createProject(
        @Part("title") title: RequestBody,
        @Part("role") role: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("projectLink") projectLink: RequestBody?,
        @Part("skills") skills: RequestBody?,
        @Part media: List<MultipartBody.Part>,
        @Part("mediaItems") mediaItems: RequestBody?
    ): ProjectDto
    
    @Multipart
    @PUT("talent/portfolio/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Part("title") title: RequestBody?,
        @Part("role") role: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("projectLink") projectLink: RequestBody?,
        @Part("skills") skills: RequestBody?,
        @Part media: List<MultipartBody.Part>,
        @Part("mediaItems") mediaItems: RequestBody?
    ): ProjectDto
    
    @DELETE("talent/portfolio/{id}")
    suspend fun deleteProject(@Path("id") id: String): Unit
}

