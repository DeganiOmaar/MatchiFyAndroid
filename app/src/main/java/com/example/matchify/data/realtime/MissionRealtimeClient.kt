package com.example.matchify.data.realtime

import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.mission.MissionDto
import com.example.matchify.data.remote.dto.mission.toDomain
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class MissionRealtimeClient(
    private val prefs: AuthPreferences,
    private val baseUrl: String = "http://10.0.2.2:3000"
) {

    private val gson = Gson()
    private val okHttpClient = OkHttpClient()
    private val _events = MutableSharedFlow<MissionRealtimeEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<MissionRealtimeEvent> = _events

    private var eventSource: EventSource? = null

    fun connect() {
        if (eventSource != null) return

        val token = prefs.currentAccessToken.value ?: return

        val request = Request.Builder()
            .url("$baseUrl/missions/stream")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "text/event-stream")
            .build()

        eventSource = EventSources.createFactory(okHttpClient)
            .newEventSource(request, object : EventSourceListener() {
                override fun onOpen(eventSource: EventSource, response: Response) {
                    Log.d("MissionRealtimeClient", "SSE connection opened")
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    runCatching {
                        val payload = gson.fromJson(data, MissionRealtimePayload::class.java)
                        handlePayload(payload)
                    }.onFailure {
                        Log.e("MissionRealtimeClient", "Failed to parse SSE event", it)
                    }
                }

                override fun onClosed(eventSource: EventSource) {
                    Log.d("MissionRealtimeClient", "SSE connection closed")
                    this@MissionRealtimeClient.eventSource = null
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    Log.e("MissionRealtimeClient", "SSE failure", t)
                    this@MissionRealtimeClient.eventSource = null
                }
            })
    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
    }

    private fun handlePayload(payload: MissionRealtimePayload) {
        when (payload.type) {
            "mission_created" -> payload.mission?.let {
                _events.tryEmit(MissionRealtimeEvent.MissionCreated(it.toDomain()))
            }

            "mission_updated" -> payload.mission?.let {
                _events.tryEmit(MissionRealtimeEvent.MissionUpdated(it.toDomain()))
            }

            "mission_deleted" -> payload.missionId?.let {
                _events.tryEmit(MissionRealtimeEvent.MissionDeleted(it))
            }
        }
    }

    private data class MissionRealtimePayload(
        val type: String,
        val mission: MissionDto? = null,
        val missionId: String? = null
    )
}

