package com.example.matchify.data.remote

import com.example.matchify.domain.model.Offer
import com.example.matchify.domain.model.UpdateOfferRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface OfferApi {
    
    @GET("offers")
    suspend fun getOffers(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): List<Offer>
    
    @GET("offers/{id}")
    suspend fun getOfferById(@Path("id") id: String): Offer
    
    @Multipart
    @JvmSuppressWildcards
    @POST("offers")
    suspend fun createOffer(
        @Part("category") category: RequestBody,
        @Part("title") title: RequestBody,
        @Part("keywords") keywords: List<RequestBody>,
        @Part("price") price: RequestBody,
        @Part("description") description: RequestBody,
        @Part banner: MultipartBody.Part,
        @Part("capabilities") capabilities: List<RequestBody>? = null,
        @Part gallery: List<MultipartBody.Part>? = null,
        @Part video: MultipartBody.Part? = null
    ): Offer
    
    @Multipart
    @JvmSuppressWildcards
    @PUT("offers/{id}")
    suspend fun updateOffer(
        @Path("id") id: String,
        @Part("category") category: RequestBody? = null,
        @Part("title") title: RequestBody? = null,
        @Part("keywords") keywords: List<RequestBody>? = null,
        @Part("price") price: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part("capabilities") capabilities: List<RequestBody>? = null,
        @Part banner: MultipartBody.Part? = null,
        @Part gallery: List<MultipartBody.Part>? = null,
        @Part video: MultipartBody.Part? = null
    ): Offer
    
    @DELETE("offers/{id}")
    suspend fun deleteOffer(@Path("id") id: String): Offer
}
