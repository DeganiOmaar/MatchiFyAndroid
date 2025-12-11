package com.example.matchify.ui.app

import android.content.Context
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.matchify.navigation.AppNavGraph

@Composable
fun AppEntry(context: Context = LocalContext.current) {

    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val provider = AppStartDestinationProvider(context)
        startDestination = provider.getStartDestination()
    }

    // WAIT until we know where to navigate
    if (startDestination != null) {
        AppNavGraph(
            navController = navController,
            startDestination = startDestination!!
        )
    }
}