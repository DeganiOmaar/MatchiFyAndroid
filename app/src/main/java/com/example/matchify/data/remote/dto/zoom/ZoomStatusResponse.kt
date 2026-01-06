package com.example.matchify.data.remote.dto.zoom

import com.google.gson.annotations.SerializedName

data class ZoomStatusResponse(
    @SerializedName("connected") val connected: Boolean,
    @SerializedName("zoomConnected") val zoomConnected: Boolean? = null
)

