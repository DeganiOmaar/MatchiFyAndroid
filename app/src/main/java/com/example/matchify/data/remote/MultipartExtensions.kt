package com.example.matchify.data.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

/**
 * Extension function to convert a String to RequestBody for multipart/form-data
 */
fun String.toMultipartString(): RequestBody {
    return RequestBody.create("text/plain".toMediaTypeOrNull(), this)
}

