package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Offer(
    @SerializedName("_id")
    val id: String,
    val category: String,
    val title: String,
    val keywords: List<String>,
    val price: Int,
    val description: String,
    val bannerImage: String,
    val galleryImages: List<String>? = null,
    val introductionVideo: String? = null,
    val capabilities: List<String>? = null,
    val talentId: String,
    val dateOfPosting: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val formattedPrice: String
        get() {
            val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)
            return "${formatter.format(price)} €"
        }

    val formattedDate: String
        get() {
            val dateString = dateOfPosting ?: createdAt ?: return "-"
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE)
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "-"
            } catch (e: Exception) {
                "-"
            }
        }

    val hasVideo: Boolean
        get() = !introductionVideo.isNullOrEmpty()

    val hasGallery: Boolean
        get() = !galleryImages.isNullOrEmpty()
}

enum class OfferCategory(val displayName: String, val iconRes: String) {
    DEVELOPMENT("Development", "code"),
    MARKETING("Marketing", "campaign"),
    TEACHING_ONLINE("Teaching Online", "school"),
    VIDEO_EDITING("Video Editing", "movie"),
    COACHING("Coaching", "groups");

    companion object {
        fun fromString(value: String): OfferCategory? {
            return values().find { it.displayName.equals(value, ignoreCase = true) }
        }
    }
}

data class CreateOfferRequest(
    val category: String,
    val title: String,
    val keywords: List<String>,
    val price: Int,
    val description: String,
    val capabilities: List<String>? = null
)

data class UpdateOfferRequest(
    val title: String,
    val description: String,
    val price: Double,
    val keywords: List<String>,
    val capabilities: List<String>? = null
)

data class OffersResponse(
    val offers: List<Offer>
)
