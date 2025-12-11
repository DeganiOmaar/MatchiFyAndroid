package com.example.matchify.data.realtime

import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.profile.RecruiterUserDto
import com.example.matchify.data.remote.dto.profile.TalentUserDto
import com.example.matchify.data.remote.dto.profile.toDomain
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class ProfileRealtimeClient(
    private val prefs: AuthPreferences,
    private val baseUrl: String = "http://10.0.2.2:3000"
) {
    private val gson = Gson()
    private val okHttpClient = OkHttpClient()
    private val _events = MutableSharedFlow<ProfileRealtimeEvent>(extraBufferCapacity = 32)
    val events: SharedFlow<ProfileRealtimeEvent> = _events

    private var eventSource: EventSource? = null

    fun connect() {
        if (eventSource != null) return

        val token = prefs.currentAccessToken.value ?: return
        val role = prefs.currentRole.value ?: return

        val streamPath = when (role) {
            "recruiter" -> "/recruiter/profile/stream"
            "talent" -> "/talent/profile/stream"
            else -> return
        }

        val request = Request.Builder()
            .url("$baseUrl$streamPath")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "text/event-stream")
            .build()

        eventSource = EventSources.createFactory(okHttpClient)
            .newEventSource(request, object : EventSourceListener() {
                override fun onOpen(eventSource: EventSource, response: Response) {
                    Log.d("ProfileRealtimeClient", "SSE connection opened for $role")
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    runCatching {
                        val payload = gson.fromJson(data, ProfileRealtimePayload::class.java)
                        handlePayload(payload, role)
                    }.onFailure {
                        Log.e("ProfileRealtimeClient", "Failed to parse SSE event", it)
                    }
                }

                override fun onClosed(eventSource: EventSource) {
                    Log.d("ProfileRealtimeClient", "SSE connection closed")
                    this@ProfileRealtimeClient.eventSource = null
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    Log.e("ProfileRealtimeClient", "SSE failure", t)
                    this@ProfileRealtimeClient.eventSource = null
                }
            })
    }

    fun disconnect() {
        eventSource?.cancel()
        eventSource = null
    }

    private fun handlePayload(payload: ProfileRealtimePayload, role: String) {
        when (payload.type) {
            "profile_updated" -> {
                val userModel = when (role) {
                    "recruiter" -> payload.recruiterUser?.toDomain()
                    "talent" -> payload.talentUser?.toDomain()
                    else -> null
                }
                userModel?.let {
                    _events.tryEmit(ProfileRealtimeEvent.ProfileUpdated(it))
                }
            }
            "profile_deleted" -> payload.userId?.let {
                _events.tryEmit(ProfileRealtimeEvent.ProfileDeleted(it))
            }
        }
    }

    private data class ProfileRealtimePayload(
        val type: String,
        val recruiterUser: RecruiterUserDto? = null,
        val talentUser: TalentUserDto? = null,
        val userId: String? = null
    )
}

