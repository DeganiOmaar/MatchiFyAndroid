package com.example.matchify.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.matchify.data.local.AuthPreferencesProvider

// CompositionLocal qui expose le code langue courant ("fr", "en", ...)
val LocalAppLanguage = staticCompositionLocalOf { "fr" }

@Composable
fun AppLanguageProvider(content: @Composable () -> Unit) {
    val prefs = AuthPreferencesProvider.getInstance().get()
    // On écoute directement le flow de langue stocké dans DataStore
    val languageCodeFlow = prefs.language
    val languageCode by languageCodeFlow.collectAsState(initial = "fr")

    CompositionLocalProvider(LocalAppLanguage provides (languageCode ?: "fr")) {
        content()
    }
}


