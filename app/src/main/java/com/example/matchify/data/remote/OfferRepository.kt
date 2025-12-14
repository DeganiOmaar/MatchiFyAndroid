package com.example.matchify.data.remote

import android.net.Uri
import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.domain.model.Offer
import com.example.matchify.domain.model.AddReviewRequest
import com.example.matchify.domain.model.UpdateOfferRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class OfferRepository(
    private val api: OfferApi,
    private val prefs: AuthPreferences
) {
    
    suspend fun getOffers(category: String? = null, search: String? = null): List<Offer> = 
        withContext(Dispatchers.IO) {
            api.getOffers(category, search)
        }
    
    suspend fun getOfferById(id: String): Offer = withContext(Dispatchers.IO) {
        api.getOfferById(id)
    }
    
    suspend fun createOffer(
        category: String,
        title: String,
        keywords: List<String>,
        price: Int,
        description: String,
        bannerImageFile: File,
        capabilities: List<String>? = null,
        galleryImageFiles: List<File>? = null,
        videoFile: File? = null
    ): Offer = withContext(Dispatchers.IO) {
        // Create request bodies for text fields
        val categoryBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
        val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        
        // Create request bodies for keywords array
        val keywordsBody = keywords.map { keyword ->
            keyword.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        
        // Create request bodies for capabilities array (if provided)
        val capabilitiesBody = capabilities?.map { capability ->
            capability.toRequestBody("text/plain".toMediaTypeOrNull())
        }
        
        // Create multipart for banner image
        val bannerRequestBody = bannerImageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val bannerPart = MultipartBody.Part.createFormData(
            "banner",
            bannerImageFile.name,
            bannerRequestBody
        )
        
        // Create multipart for gallery images (if provided)
        val galleryParts = galleryImageFiles?.map { file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gallery", file.name, requestBody)
        }
        
        // Create multipart for video (if provided)
        val videoPart = videoFile?.let { file ->
            val requestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("video", file.name, requestBody)
        }
        
        Log.d("OfferRepository", "Creating offer with category: $category, title: $title, price: $price")
        
        api.createOffer(
            category = categoryBody,
            title = titleBody,
            keywords = keywordsBody,
            price = priceBody,
            description = descriptionBody,
            banner = bannerPart,
            capabilities = capabilitiesBody,
            gallery = galleryParts,
            video = videoPart
        )
    }
    
    suspend fun updateOffer(
        id: String,
        category: String? = null,
        title: String? = null,
        keywords: List<String>? = null,
        price: Int? = null,
        description: String? = null,
        capabilities: List<String>? = null,
        bannerImageFile: File? = null,
        galleryImageFiles: List<File>? = null,
        videoFile: File? = null
    ): Offer = withContext(Dispatchers.IO) {
        val categoryBody = category?.toRequestBody("text/plain".toMediaTypeOrNull())
        val titleBody = title?.toRequestBody("text/plain".toMediaTypeOrNull())
        val priceBody = price?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        
        val keywordsBody = keywords?.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        val capabilitiesBody = capabilities?.map { it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        
        val bannerPart = bannerImageFile?.let { file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("banner", file.name, requestBody)
        }
        
        val galleryParts = galleryImageFiles?.map { file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("gallery", file.name, requestBody)
        }
        
        val videoPart = videoFile?.let { file ->
            val requestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("video", file.name, requestBody)
        }
        
        api.updateOffer(
            id = id,
            category = categoryBody,
            title = titleBody,
            keywords = keywordsBody,
            price = priceBody,
            description = descriptionBody,
            capabilities = capabilitiesBody,
            banner = bannerPart,
            gallery = galleryParts,
            video = videoPart
        )
    }
    
    suspend fun deleteOffer(id: String): Offer = withContext(Dispatchers.IO) {
        api.deleteOffer(id)
    }
    
    suspend fun addReview(id: String, rating: Int, message: String): Offer = withContext(Dispatchers.IO) {
        val request = AddReviewRequest(rating, message)
        api.addReview(id, request)
    }
    
    fun isOfferOwner(offer: Offer): Boolean {
        val currentUser = prefs.currentUser.value
        val currentUserId = currentUser?.id
        
        Log.d("OfferRepository", "isOfferOwner - currentUserId: $currentUserId, offer.talentId: ${offer.talentId}")
        
        if (currentUserId == null || currentUserId.isEmpty()) {
            Log.d("OfferRepository", "isOfferOwner - currentUserId is null or empty")
            return false
        }
        
        val isOwner = offer.talentId == currentUserId
        Log.d("OfferRepository", "isOfferOwner - result: $isOwner")
        
        return isOwner
    }
}
