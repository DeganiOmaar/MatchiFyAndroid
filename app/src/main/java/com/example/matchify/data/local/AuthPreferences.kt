package com.example.matchify.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.matchify.domain.model.UserModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("auth_prefs")

class AuthPreferences(private val context: Context) {

    private val gson = Gson()

    // Define TOKEN key before it is used
    companion object {
        private val TOKEN = stringPreferencesKey("token")
        private val USER = stringPreferencesKey("user_json")
        private val ROLE = stringPreferencesKey("user_role")
        private val REMEMBER = booleanPreferencesKey("remember_me")
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    // Define the token flow before the init block
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN] }

    // Define user flow before the init block
    val user: Flow<UserModel?> = context.dataStore.data.map { prefs ->
        prefs[USER]?.let { gson.fromJson(it, UserModel::class.java) }
    }

    val role: Flow<String?> = context.dataStore.data.map { it[ROLE] }

    val rememberMe: Flow<Boolean> = context.dataStore.data.map {
        it[REMEMBER] ?: false
    }
    
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map {
        it[HAS_SEEN_ONBOARDING] ?: false
    }

    private val _currentAccessToken = MutableStateFlow<String?>(null)
    val currentAccessToken: StateFlow<String?> = _currentAccessToken

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Get current user as StateFlow for easy access
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser

    init {
        // Initialize StateFlow with current token value immediately
        applicationScope.launch {
            val currentToken = token.first()
            _currentAccessToken.value = currentToken
        }
        
        // Then keep it synchronized with changes
        applicationScope.launch {
            token.collect { newToken ->
                _currentAccessToken.value = newToken
            }
        }
        
        // Initialize current user
        applicationScope.launch {
            val currentUserValue = user.first()
            _currentUser.value = currentUserValue
        }
        
        // Keep user synchronized
        applicationScope.launch {
            user.collect { newUser ->
                _currentUser.value = newUser
            }
        }
    }

    // Save Token
    suspend fun saveToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token == null) {
                prefs.remove(TOKEN)
            } else {
                prefs[TOKEN] = token
            }
        }
    }

    // Save User as JSON
    suspend fun saveUser(user: UserModel?) {
        context.dataStore.edit { prefs ->
            if (user == null) {
                prefs.remove(USER)
                prefs.remove(ROLE)
            } else {
                prefs[USER] = gson.toJson(user)
                prefs[ROLE] = user.role
            }
        }
    }

    suspend fun saveRememberMe(state: Boolean) {
        context.dataStore.edit { it[REMEMBER] = state }
    }
    
    suspend fun saveHasSeenOnboarding(hasSeen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_ONBOARDING] = hasSeen }
    }

    // Startup helpers
    suspend fun getTokenValue(): String? = token.first()

    suspend fun getRememberMeValue(): Boolean = rememberMe.first()

    suspend fun getRoleValue(): String? = role.first()
    
    suspend fun getHasSeenOnboardingValue(): Boolean = hasSeenOnboarding.first()

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
        _currentAccessToken.value = null // Explicitly clear on logout
        _currentUser.value = null // Clear current user on logout
    }
}