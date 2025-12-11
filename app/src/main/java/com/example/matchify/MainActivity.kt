package com.example.matchify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.ui.app.AppEntry
import com.example.matchify.ui.app.AppLanguageProvider
import com.example.matchify.ui.theme.MatchiFyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AuthPreferencesProvider if not already initialized
        AuthPreferencesProvider.initialize(applicationContext)

        setContent {
            MatchiFyTheme {
                AppLanguageProvider {
                    AppEntry(context = this)
                }
            }
        }
    }
}