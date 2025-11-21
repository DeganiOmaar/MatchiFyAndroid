package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName

data class MediaItem(
    @SerializedName("type") val type: String, // 'image', 'video', 'pdf', 'external_link'
    @SerializedName("url") val url: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("externalLink") val externalLink: String? = null
) {
    val isImage: Boolean
        get() = type == "image"
    
    val isVideo: Boolean
        get() = type == "video"
    
    val isPdf: Boolean
        get() = type == "pdf"
    
    val isExternalLink: Boolean
        get() = type == "external_link"
    
    fun getMediaUrl(baseUrl: String): String? {
        if (isExternalLink && externalLink != null) {
            return externalLink
        }
        
        val path = url?.trim() ?: return null
        if (path.isEmpty()) return null
        
        val normalized = if (path.startsWith("/")) path else "/$path"
        return "$baseUrl$normalized"
    }
}

