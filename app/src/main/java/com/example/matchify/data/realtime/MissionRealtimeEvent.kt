package com.example.matchify.data.realtime

import com.example.matchify.domain.model.Mission

sealed class MissionRealtimeEvent {
    data class MissionCreated(val mission: Mission) : MissionRealtimeEvent()
    data class MissionUpdated(val mission: Mission) : MissionRealtimeEvent()
    data class MissionDeleted(val missionId: String) : MissionRealtimeEvent()
}

