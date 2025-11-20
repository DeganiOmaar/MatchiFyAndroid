package com.example.matchify.data.remote.dto.portfolio

import com.google.gson.annotations.SerializedName

data class MediaItemDto(
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("externalLink") val externalLink: String? = null
)

