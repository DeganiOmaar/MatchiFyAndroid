package com.example.matchify.ui.missions.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Mission
import com.example.matchify.ui.missions.components.MissionRow
import com.example.matchify.data.local.AuthPreferencesProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionListScreen(
    onAddMission: () -> Unit,
    onEditMission: (Mission) -> Unit,
    onMissionClick: (Mission) -> Unit = {},
    viewModel: MissionListViewModel = viewModel(factory = MissionListViewModelFactory())
) {
    val missions by viewModel.missions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Get user role
    val context = LocalContext.current
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val userRole by prefs.role.collectAsState(initial = "recruiter")
    val isRecruiter = userRole == "recruiter"

    LaunchedEffect(Unit) {
        viewModel.loadMissions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Missions") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && missions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                missions.isEmpty() -> {
                    EmptyMissionsView(isRecruiter)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(missions) { mission ->
                            val isOwner = viewModel.isMissionOwner(mission)

                            MissionRow(
                                mission = mission,
                                isOwner = isOwner && isRecruiter,
                                isRecruiter = isRecruiter,
                                onClick = { onMissionClick(mission) },
                                onEdit = { onEditMission(mission) },
                                onDelete = { viewModel.deleteMission(mission) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMissionsView(isRecruiter: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Work,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "No Missions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isRecruiter) {
                    "You have not created any missions yet."
                } else {
                    "No missions available at the moment."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}