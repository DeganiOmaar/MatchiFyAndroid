package com.example.matchify.data.realtime

import com.example.matchify.domain.model.UserModel

sealed class ProfileRealtimeEvent {
    data class ProfileUpdated(val user: UserModel) : ProfileRealtimeEvent()
    data class ProfileDeleted(val userId: String) : ProfileRealtimeEvent()
}

