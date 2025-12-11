package com.example.matchify.data.remote

import android.content.ContentResolver
import android.net.Uri
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.portfolio.toDomain
import com.example.matchify.domain.model.MediaItem
import com.example.matchify.domain.model.Project
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayInputStream
import java.io.InputStream

class PortfolioRepository(
    private val api: PortfolioApi,
    private val prefs: AuthPreferences,
    private val contentResolver: ContentResolver? = null
) {
    
    suspend fun getProjects(): List<Project> = withContext(Dispatchers.IO) {
        val response = api.getProjects()
        val currentUserId = prefs.currentUser.value?.id
        response.projects.map { it.toDomain(currentUserId) }
    }
    
    suspend fun getProjectById(id: String): Project = withContext(Dispatchers.IO) {
        val currentUserId = prefs.currentUser.value?.id
        android.util.Log.d("PortfolioRepository", "Fetching project with ID: $id")
        val response = api.getProjectById(id)
        android.util.Log.d("PortfolioRepository", "ProjectResponseDto received: message=${response.message}, project title=${response.project.title}")
        val projectDto = response.project
        android.util.Log.d("PortfolioRepository", "ProjectDto extracted: title=${projectDto.title}, description=${projectDto.description?.take(50)}, media=${projectDto.media?.size ?: 0}, skills=${projectDto.skills?.size ?: 0}")
        val project = projectDto.toDomain(currentUserId)
        android.util.Log.d("PortfolioRepository", "Project mapped: title=${project.title}, description=${project.description?.take(50)}, media=${project.media.size}, skills=${project.skills.size}")
        project
    }
    
    suspend fun createProject(
        title: String,
        role: String?,
        skills: List<String>?,
        description: String?,
        projectLink: String?,
        mediaItems: List<com.example.matchify.ui.portfolio.add.AttachedMediaItem>
    ): Project = withContext(Dispatchers.IO) {
        val currentUserId = prefs.currentUser.value?.id
        
        // Prepare form fields
        val titleBody = title.toMultipartString()
        val roleBody = role?.toMultipartString()
        val descriptionBody = description?.toMultipartString()
        val projectLinkBody = projectLink?.toMultipartString()
        
        // Skills as JSON array
        val skillsBody = skills?.takeIf { it.isNotEmpty() }?.let {
            val gson = Gson()
            val jsonString = gson.toJson(it)
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
        }
        
        // Convert media items to multipart parts
        val newMediaParts = mutableListOf<MultipartBody.Part>()
        val existingMediaItems = mutableListOf<MediaItem>()
        
        mediaItems.forEach { attachedMedia ->
            when (attachedMedia) {
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ImageMedia -> {
                    attachedMedia.imageData?.let { data ->
                        val body = RequestBody.create("image/jpeg".toMediaTypeOrNull(), data)
                        val filename = "portfolio-image-${java.util.UUID.randomUUID()}.jpg"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.VideoMedia -> {
                    contentResolver?.openInputStream(attachedMedia.uri)?.use { inputStream ->
                        val data = inputStream.readBytes()
                        val body = RequestBody.create("video/*".toMediaTypeOrNull(), data)
                        val filename = "portfolio-video-${java.util.UUID.randomUUID()}.mp4"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.PdfMedia -> {
                    contentResolver?.openInputStream(attachedMedia.uri)?.use { inputStream ->
                        val data = inputStream.readBytes()
                        val body = RequestBody.create("application/pdf".toMediaTypeOrNull(), data)
                        val filename = "portfolio-pdf-${java.util.UUID.randomUUID()}.pdf"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ExternalLinkMedia -> {
                    existingMediaItems.add(
                        MediaItem(
                            type = "external_link",
                            url = null,
                            title = attachedMedia.title,
                            externalLink = attachedMedia.url
                        )
                    )
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ExistingMedia -> {
                    existingMediaItems.add(attachedMedia.mediaItem)
                }
            }
        }
        
        // Existing media items as JSON
        val mediaItemsBody = if (existingMediaItems.isNotEmpty()) {
            val gson = Gson()
            val mediaItemsArray = existingMediaItems.map { item ->
                mapOf(
                    "type" to item.type,
                    "url" to (item.url ?: ""),
                    "title" to (item.title ?: ""),
                    "externalLink" to (item.externalLink ?: "")
                )
            }
            val jsonString = gson.toJson(mediaItemsArray)
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
        } else {
            null
        }
        
        val response = api.createProject(
            title = titleBody,
            role = roleBody,
            description = descriptionBody,
            projectLink = projectLinkBody,
            skills = skillsBody,
            media = newMediaParts,
            mediaItems = mediaItemsBody
        )
        
        response.toDomain(currentUserId)
    }
    
    suspend fun updateProject(
        id: String,
        title: String?,
        role: String?,
        skills: List<String>?,
        description: String?,
        projectLink: String?,
        mediaItems: List<com.example.matchify.ui.portfolio.add.AttachedMediaItem>
    ): Project = withContext(Dispatchers.IO) {
        val currentUserId = prefs.currentUser.value?.id
        
        // Prepare form fields
        val titleBody = title?.toMultipartString()
        val roleBody = role?.toMultipartString()
        val descriptionBody = description?.toMultipartString()
        val projectLinkBody = projectLink?.toMultipartString()
        
        // Skills as JSON array
        val skillsBody = skills?.takeIf { it.isNotEmpty() }?.let {
            val gson = Gson()
            val jsonString = gson.toJson(it)
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
        }
        
        // Convert media items to multipart parts
        val newMediaParts = mutableListOf<MultipartBody.Part>()
        val existingMediaItems = mutableListOf<MediaItem>()
        
        mediaItems.forEach { attachedMedia ->
            when (attachedMedia) {
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ImageMedia -> {
                    attachedMedia.imageData?.let { data ->
                        val body = RequestBody.create("image/jpeg".toMediaTypeOrNull(), data)
                        val filename = "portfolio-image-${java.util.UUID.randomUUID()}.jpg"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.VideoMedia -> {
                    contentResolver?.openInputStream(attachedMedia.uri)?.use { inputStream ->
                        val data = inputStream.readBytes()
                        val body = RequestBody.create("video/*".toMediaTypeOrNull(), data)
                        val filename = "portfolio-video-${java.util.UUID.randomUUID()}.mp4"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.PdfMedia -> {
                    contentResolver?.openInputStream(attachedMedia.uri)?.use { inputStream ->
                        val data = inputStream.readBytes()
                        val body = RequestBody.create("application/pdf".toMediaTypeOrNull(), data)
                        val filename = "portfolio-pdf-${java.util.UUID.randomUUID()}.pdf"
                        newMediaParts.add(MultipartBody.Part.createFormData("media", filename, body))
                    }
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ExternalLinkMedia -> {
                    existingMediaItems.add(
                        MediaItem(
                            type = "external_link",
                            url = null,
                            title = attachedMedia.title,
                            externalLink = attachedMedia.url
                        )
                    )
                }
                is com.example.matchify.ui.portfolio.add.AttachedMediaItem.ExistingMedia -> {
                    existingMediaItems.add(attachedMedia.mediaItem)
                }
            }
        }
        
        // Existing media items as JSON
        val mediaItemsBody = if (existingMediaItems.isNotEmpty()) {
            val gson = Gson()
            val mediaItemsArray = existingMediaItems.map { item ->
                mapOf(
                    "type" to item.type,
                    "url" to (item.url ?: ""),
                    "title" to (item.title ?: ""),
                    "externalLink" to (item.externalLink ?: "")
                )
            }
            val jsonString = gson.toJson(mediaItemsArray)
            RequestBody.create("application/json".toMediaTypeOrNull(), jsonString)
        } else {
            null
        }
        
        val response = api.updateProject(
            id = id,
            title = titleBody,
            role = roleBody,
            description = descriptionBody,
            projectLink = projectLinkBody,
            skills = skillsBody,
            media = newMediaParts,
            mediaItems = mediaItemsBody
        )
        
        response.toDomain(currentUserId)
    }
    
    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        api.deleteProject(id)
    }
}

