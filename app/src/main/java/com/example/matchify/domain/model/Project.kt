package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Project(
    @SerializedName("_id") val id: String? = null,
    val id_alt: String? = null,
    @SerializedName("talentId") val talentId: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("role") val role: String? = null,
    @SerializedName("media") val media: List<MediaItem> = emptyList(),
    @SerializedName("skills") val skills: List<String> = emptyList(),
    @SerializedName("description") val description: String? = null,
    @SerializedName("projectLink") val projectLink: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    val projectId: String
        get() = id ?: id_alt ?: UUID.randomUUID().toString()
    
    val firstMediaItem: MediaItem?
        get() = media.firstOrNull()
    
    fun getFirstMediaUrl(baseUrl: String): String? {
        return firstMediaItem?.getMediaUrl(baseUrl)
    }
    
    val images: List<MediaItem>
        get() = media.filter { it.isImage }
    
    val videos: List<MediaItem>
        get() = media.filter { it.isVideo }
    
    val pdfs: List<MediaItem>
        get() = media.filter { it.isPdf }
    
    val externalLinks: List<MediaItem>
        get() = media.filter { it.isExternalLink }
}

