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
    }

    // Define the token flow before the init block
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN] }

    private val _currentAccessToken = MutableStateFlow<String?>(null)
    val currentAccessToken: StateFlow<String?> = _currentAccessToken

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    val user: Flow<UserModel?> = context.dataStore.data.map { prefs ->
        prefs[USER]?.let { gson.fromJson(it, UserModel::class.java) }
    }

    val role: Flow<String?> = context.dataStore.data.map { it[ROLE] }

    suspend fun saveRememberMe(state: Boolean) {
        context.dataStore.edit { it[REMEMBER] = state }
    }

    val rememberMe: Flow<Boolean> = context.dataStore.data.map {
        it[REMEMBER] ?: false
    }

    // Startup helpers
    suspend fun getTokenValue(): String? = token.first()

    suspend fun getRememberMeValue(): Boolean = rememberMe.first()

    suspend fun getRoleValue(): String? = role.first()

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
        _currentAccessToken.value = null // Explicitly clear on logout
    }
}