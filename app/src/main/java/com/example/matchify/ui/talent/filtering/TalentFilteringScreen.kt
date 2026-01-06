package com.example.matchify.ui.talent.filtering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.talent.filtering.components.TalentCandidateCard
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RatingRepository
import kotlinx.coroutines.launch

/**
 * Écran pour afficher les talents filtrés avec IA pour une mission
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentFilteringScreen(
    missionId: String,
    onTalentClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: TalentFilteringViewModel = viewModel(factory = TalentFilteringViewModelFactory())
) {
    val candidates by viewModel.candidates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val totalResults by viewModel.totalResults.collectAsState()
    
    // Gestion des ratings
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val ratingRepository = remember {
        val apiService = ApiService.getInstance()
        RatingRepository(apiService.ratingApi)
    }
    val talentRatings = remember { mutableStateMapOf<String, Double?>() }
    val scope = rememberCoroutineScope()
    
    // Charger les candidats au démarrage
    LaunchedEffect(missionId) {
        viewModel.filterTalentsForMission(missionId)
    }
    
    // Charger les ratings pour chaque candidat
    LaunchedEffect(candidates) {
        candidates.forEach { candidate ->
            if (!talentRatings.containsKey(candidate.talentId)) {
                scope.launch {
                    try {
                        val ratingsResponse = ratingRepository.getTalentRatings(candidate.talentId)
                        val rating = ratingsResponse.bayesianScore ?: ratingsResponse.averageScore
                        // Convertir le score sur 5 en pourcentage (multiplier par 20)
                        talentRatings[candidate.talentId] = rating?.let { it * 20.0 }
                    } catch (e: Exception) {
                        android.util.Log.e("TalentFilteringScreen", "Error loading rating for ${candidate.talentId}: ${e.message}", e)
                        talentRatings[candidate.talentId] = null
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Talents Recommandés",
                            fontWeight = FontWeight.Bold
                        )
                        totalResults?.let { total ->
                            Text(
                                text = "$total candidat${if (total > 1) "s" else ""} trouvé${if (total > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0F172A))
        ) {
            when {
                isLoading && candidates.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
                
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "Une erreur est survenue",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 16.sp
                            )
                            Button(
                                onClick = {
                                    viewModel.filterTalentsForMission(missionId)
                                }
                            ) {
                                Text("Réessayer")
                            }
                        }
                    }
                }
                
                candidates.isEmpty() -> {
                    EmptyCandidatesView()
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(candidates) { candidate ->
                            TalentCandidateCard(
                                candidate = candidate,
                                onClick = { onTalentClick(candidate.talentId) },
                                ratingPercentage = talentRatings[candidate.talentId]
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCandidatesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "Aucun candidat trouvé",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Aucun talent ne correspond aux critères de la mission.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

