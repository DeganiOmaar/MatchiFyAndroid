package com.example.matchify.ui.talent.filtering.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.ui.talent.filtering.TalentFilteringViewModel
import com.example.matchify.ui.talent.filtering.TalentFilteringViewModelFactory

/**
 * Écran de test pour valider le modèle IA de filtrage des talents
 * Permet de tester différentes missions et critères pour valider les résultats
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentFilteringTestScreen(
    onBack: () -> Unit = {},
    viewModel: TalentFilteringViewModel = viewModel(factory = TalentFilteringViewModelFactory())
) {
    val candidates by viewModel.candidates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val totalResults by viewModel.totalResults.collectAsState()
    
    var missionIdInput by remember { mutableStateOf("") }
    var minScoreInput by remember { mutableStateOf("") }
    var testResults by remember { mutableStateOf<List<TestResult>>(emptyList()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Test Modèle IA - Filtrage Talents",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section de test
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Paramètres de Test",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        OutlinedTextField(
                            value = missionIdInput,
                            onValueChange = { missionIdInput = it },
                            label = { Text("Mission ID") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                        
                        OutlinedTextField(
                            value = minScoreInput,
                            onValueChange = { minScoreInput = it },
                            label = { Text("Score Minimum (optionnel)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF475569)
                            )
                        )
                        
                        Button(
                            onClick = {
                                if (missionIdInput.isNotEmpty()) {
                                    val minScore = minScoreInput.toIntOrNull()
                                    viewModel.filterTalentsForMission(
                                        missionId = missionIdInput,
                                        minScore = minScore
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = missionIdInput.isNotEmpty() && !isLoading
                        ) {
                            Text("Tester le Modèle")
                        }
                    }
                }
            }
            
            // Résultats du test
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                    }
                }
            }
            
            item {
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Error,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Erreur",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = errorMessage ?: "Erreur inconnue",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // Statistiques des résultats
            item {
                if (candidates.isNotEmpty()) {
                    val stats = calculateStatistics(candidates)
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Statistiques des Résultats",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            StatRow("Total candidats", "${candidates.size}")
                            StatRow("Score moyen", "${stats.averageScore.toInt()}%")
                            StatRow("Score min", "${stats.minScore}%")
                            StatRow("Score max", "${stats.maxScore}%")
                            StatRow("High Match (≥80%)", "${stats.highMatchCount}")
                            StatRow("Good Match (60-79%)", "${stats.goodMatchCount}")
                            StatRow("Low Match (<60%)", "${stats.lowMatchCount}")
                            
                            // Validation
                            val validation = validateResults(candidates)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFF475569))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Validation du Modèle",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            validation.forEach { result ->
                                ValidationResultItem(result)
                            }
                        }
                    }
                }
            }
            
            // Liste des candidats
            items(candidates) { candidate ->
                CandidateTestCard(candidate)
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ValidationResultItem(result: ValidationResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (result.isValid) Icons.Filled.CheckCircle else Icons.Filled.Error,
            contentDescription = null,
            tint = if (result.isValid) Color(0xFF10B981) else Color(0xFFEF4444),
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.label,
                color = Color.White,
                fontSize = 14.sp
            )
            if (!result.isValid) {
                Text(
                    text = result.message ?: "",
                    color = Color(0xFFEF4444),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CandidateTestCard(candidate: com.example.matchify.domain.model.TalentCandidate) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = candidate.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    color = Color(candidate.scoreColor).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${candidate.score}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(candidate.scoreColor),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            candidate.reasons?.let { reasons ->
                Text(
                    text = reasons,
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            candidate.matchBreakdown?.let { breakdown ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breakdown.skillsMatch?.let {
                        BreakdownItem("Skills", it)
                    }
                    breakdown.experienceMatch?.let {
                        BreakdownItem("Exp", it)
                    }
                    breakdown.locationMatch?.let {
                        BreakdownItem("Loc", it)
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakdownItem(label: String, value: Double) {
    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${value.toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B82F6)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * Statistiques des résultats
 */
data class Statistics(
    val averageScore: Double,
    val minScore: Int,
    val maxScore: Int,
    val highMatchCount: Int,
    val goodMatchCount: Int,
    val lowMatchCount: Int
)

/**
 * Résultat de validation
 */
data class ValidationResult(
    val label: String,
    val isValid: Boolean,
    val message: String? = null
)

/**
 * Résultat de test
 */
data class TestResult(
    val missionId: String,
    val timestamp: Long,
    val candidatesCount: Int,
    val averageScore: Double,
    val isValid: Boolean
)

/**
 * Calculer les statistiques des candidats
 */
private fun calculateStatistics(candidates: List<com.example.matchify.domain.model.TalentCandidate>): Statistics {
    if (candidates.isEmpty()) {
        return Statistics(0.0, 0, 0, 0, 0, 0)
    }
    
    val scores = candidates.map { it.score }
    val averageScore = scores.average()
    val minScore = scores.minOrNull() ?: 0
    val maxScore = scores.maxOrNull() ?: 0
    
    val highMatchCount = scores.count { it >= 80 }
    val goodMatchCount = scores.count { it in 60..79 }
    val lowMatchCount = scores.count { it < 60 }
    
    return Statistics(
        averageScore = averageScore,
        minScore = minScore,
        maxScore = maxScore,
        highMatchCount = highMatchCount,
        goodMatchCount = goodMatchCount,
        lowMatchCount = lowMatchCount
    )
}

/**
 * Valider les résultats du modèle
 */
private fun validateResults(candidates: List<com.example.matchify.domain.model.TalentCandidate>): List<ValidationResult> {
    val results = mutableListOf<ValidationResult>()
    
    // Validation 1: Les scores sont dans la plage valide
    val allScoresValid = candidates.all { it.score in 0..100 }
    results.add(
        ValidationResult(
            label = "Scores dans la plage 0-100",
            isValid = allScoresValid,
            message = if (!allScoresValid) "Certains scores sont hors plage" else null
        )
    )
    
    // Validation 2: Les scores sont triés (décroissant généralement)
    val isSorted = candidates.zipWithNext().all { (a, b) -> a.score >= b.score }
    results.add(
        ValidationResult(
            label = "Scores triés par pertinence",
            isValid = isSorted,
            message = if (!isSorted) "Les scores ne sont pas triés" else null
        )
    )
    
    // Validation 3: Les raisons sont présentes pour les scores élevés
    val highScoresHaveReasons = candidates.filter { it.score >= 70 }
        .all { !it.reasons.isNullOrBlank() }
    results.add(
        ValidationResult(
            label = "Raisons présentes pour scores élevés",
            isValid = highScoresHaveReasons,
            message = if (!highScoresHaveReasons) "Certains scores élevés n'ont pas de raisons" else null
        )
    )
    
    // Validation 4: Le breakdown est cohérent avec le score global
    val breakdownConsistent = candidates.filter { it.matchBreakdown != null }
        .all { candidate ->
            val breakdown = candidate.matchBreakdown!!
            val avgBreakdown = listOfNotNull(
                breakdown.skillsMatch,
                breakdown.experienceMatch,
                breakdown.locationMatch
            ).average()
            // Le score global devrait être proche de la moyenne du breakdown (±10%)
            kotlin.math.abs(candidate.score - avgBreakdown) <= 10
        }
    results.add(
        ValidationResult(
            label = "Breakdown cohérent avec score global",
            isValid = breakdownConsistent,
            message = if (!breakdownConsistent) "Certains breakdowns ne correspondent pas au score" else null
        )
    )
    
    // Validation 5: Distribution des scores (au moins un score élevé si plusieurs candidats)
    if (candidates.size > 3) {
        val hasHighScore = candidates.any { it.score >= 80 }
        results.add(
            ValidationResult(
                label = "Présence de scores élevés",
                isValid = hasHighScore,
                message = if (!hasHighScore) "Aucun score élevé trouvé pour plusieurs candidats" else null
            )
        )
    }
    
    return results
}

