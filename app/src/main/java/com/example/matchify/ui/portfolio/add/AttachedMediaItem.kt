package com.example.matchify.ui.portfolio.add

import android.net.Uri
import com.example.matchify.domain.model.MediaItem
import java.util.*

sealed class AttachedMediaItem {
    data class ImageMedia(val uri: Uri, val imageData: ByteArray?) : AttachedMediaItem() {
        override val id: String = UUID.randomUUID().toString()
        override val displayTitle: String = "Image"
    }
    
    data class VideoMedia(val uri: Uri) : AttachedMediaItem() {
        override val id: String = uri.toString()
        override val displayTitle: String = "Video"
    }
    
    data class PdfMedia(val uri: Uri) : AttachedMediaItem() {
        override val id: String = uri.toString()
        override val displayTitle: String = "PDF"
    }
    
    data class ExternalLinkMedia(val url: String, val title: String) : AttachedMediaItem() {
        override val id: String = url
        override val displayTitle: String = title
    }
    
    data class ExistingMedia(val mediaItem: MediaItem) : AttachedMediaItem() {
        override val id: String = mediaItem.url ?: UUID.randomUUID().toString()
        override val displayTitle: String = mediaItem.title ?: mediaItem.type.replaceFirstChar { it.uppercase() }
    }
    
    abstract val id: String
    abstract val displayTitle: String
}

