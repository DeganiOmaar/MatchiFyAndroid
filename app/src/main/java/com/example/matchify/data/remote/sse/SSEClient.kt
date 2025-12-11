package com.example.matchify.data.remote.sse

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject

/**
 * SSE (Server-Sent Events) Client for Android
 * Handles real-time streaming from backend SSE endpoints
 */
class SSEClient(private val client: OkHttpClient) {

    data class SSEEvent(
        val data: String,
        val event: String? = null,
        val id: String? = null
    )

    /**
     * Connect to an SSE endpoint and return a Flow of events
     * @param url The SSE endpoint URL
     * @param token Optional authentication token
     * @return Flow of SSE events
     */
    fun connect(url: String, token: String? = null): Flow<SSEEvent> = callbackFlow {
        Log.d(TAG, "🔵 [SSE] Connecting to: $url")

        val request = Request.Builder()
            .url(url)
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }
            }
            .build()

        val eventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                Log.d(TAG, "✅ [SSE] Connection opened")
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                Log.d(TAG, "📨 [SSE] Event received: ${data.take(100)}...")
                
                val event = SSEEvent(
                    data = data,
                    event = type,
                    id = id
                )
                
                trySend(event)
            }

            override fun onClosed(eventSource: EventSource) {
                Log.d(TAG, "🔵 [SSE] Connection closed")
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                Log.e(TAG, "❌ [SSE] Connection failed: ${t?.message}", t)
                close(t)
            }
        }

        val eventSource = EventSources.createFactory(client)
            .newEventSource(request, eventSourceListener)

        awaitClose {
            Log.d(TAG, "🛑 [SSE] Closing connection")
            eventSource.cancel()
        }
    }

    companion object {
        private const val TAG = "SSEClient"
    }
}
