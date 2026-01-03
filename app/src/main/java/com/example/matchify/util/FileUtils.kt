package com.example.matchify.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.io.InputStream

object FileUtils {
    
    /**
     * Get MIME type from URI
     */
    fun getMimeType(uri: Uri, context: Context): String {
        return context.contentResolver.getType(uri) ?: run {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
        }
    }
    
    /**
     * Get file name from URI
     */
    fun getFileName(uri: Uri, context: Context): String {
        var fileName = "file"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    
    /**
     * Get file size in bytes
     */
    fun getFileSize(uri: Uri, context: Context): Long {
        var fileSize = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }
        return fileSize
    }
    
    /**
     * Validate file size (max 10MB)
     */
    fun validateFileSize(uri: Uri, context: Context, maxSizeBytes: Long = 10 * 1024 * 1024): Boolean {
        val fileSize = getFileSize(uri, context)
        return fileSize > 0 && fileSize <= maxSizeBytes
    }
    
    /**
     * Convert URI to byte array
     */
    fun uriToByteArray(uri: Uri, context: Context): ByteArray {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val byteBuffer = ByteArrayOutputStream()
        
        inputStream?.use { input ->
            val buffer = ByteArray(1024)
            var len: Int
            while (input.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
        }
        
        return byteBuffer.toByteArray()
    }
    
    /**
     * Format file size for display
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}
