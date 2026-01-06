package com.example.matchify.ui.talent.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.matchify.ui.talent.matching.components.TalentMatchCard
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RatingRepository
import kotlinx.coroutines.launch

/**
 * Écran pour afficher les talents filtrés et scorés par IA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentMatchingScreen(
    missionId: String? = null,
    onTalentClick: (String) -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: TalentMatchingViewModel = viewModel(factory = TalentMatchingViewModelFactory())
) {
    val talents by viewModel.talents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Gestion des ratings
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val ratingRepository = remember {
        val apiService = ApiService.getInstance()
        RatingRepository(apiService.ratingApi)
    }
    val talentRatings = remember { mutableStateMapOf<String, Double?>() }
    val scope = rememberCoroutineScope()
    
    // Charger les talents au démarrage si missionId est fourni
    LaunchedEffect(missionId) {
        if (missionId != null) {
            viewModel.loadMatchedTalentsForMission(missionId)
        }
    }
    
    // Charger les ratings pour chaque talent
    LaunchedEffect(talents) {
        talents.forEach { talent ->
            if (!talentRatings.containsKey(talent.talentId)) {
                scope.launch {
                    try {
                        val ratingsResponse = ratingRepository.getTalentRatings(talent.talentId)
                        val rating = ratingsResponse.bayesianScore ?: ratingsResponse.averageScore
                        // Convertir le score sur 5 en pourcentage (multiplier par 20)
                        talentRatings[talent.talentId] = rating?.let { it * 20.0 }
                    } catch (e: Exception) {
                        android.util.Log.e("TalentMatchingScreen", "Error loading rating for ${talent.talentId}: ${e.message}", e)
                        talentRatings[talent.talentId] = null
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (missionId != null) {
                            "Talents Recommandés"
                        } else {
                            "Recherche de Talents"
                        },
                        fontWeight = FontWeight.Bold
                    )
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
                isLoading && talents.isEmpty() -> {
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
                                    if (missionId != null) {
                                        viewModel.loadMatchedTalentsForMission(missionId)
                                    }
                                }
                            ) {
                                Text("Réessayer")
                            }
                        }
                    }
                }
                
                talents.isEmpty() -> {
                    EmptyTalentsView()
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(talents) { talent ->
                            TalentMatchCard(
                                talent = talent,
                                onClick = { onTalentClick(talent.talentId) },
                                ratingPercentage = talentRatings[talent.talentId]
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTalentsView() {
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
                text = "Aucun talent trouvé",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Aucun talent ne correspond aux critères de recherche.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

