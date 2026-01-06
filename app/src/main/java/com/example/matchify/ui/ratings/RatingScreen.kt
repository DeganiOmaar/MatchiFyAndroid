package com.example.matchify.ui.ratings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Rating
import com.example.matchify.ui.ratings.components.RatingCard
import com.example.matchify.ui.ratings.components.AverageRatingCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

/**
 * Écran pour créer/modifier un rating et voir les feedbacks d'autres recruteurs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    talentId: String,
    talentName: String? = null,
    missionId: String? = null,
    onBack: () -> Unit,
    viewModel: RatingViewModel = viewModel(factory = RatingViewModelFactory())
) {
    val myRating by viewModel.myRating.collectAsState()
    val talentRatings by viewModel.talentRatings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    // État local pour le formulaire
    var selectedScore by remember { mutableStateOf(myRating?.score ?: 0) }
    var comment by remember { mutableStateOf(myRating?.comment ?: "") }
    
    // Charger les données au démarrage
    LaunchedEffect(talentId, missionId) {
        viewModel.loadMyRating(talentId, missionId)
        viewModel.loadTalentRatings(talentId)
    }
    
    // Mettre à jour le formulaire quand myRating change
    LaunchedEffect(myRating) {
        myRating?.let { rating ->
            selectedScore = rating.score
            comment = rating.comment ?: ""
        }
    }
    
    // Fermer automatiquement l'écran après succès de la sauvegarde
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            // Attendre un court instant pour que l'utilisateur voie le message de succès
            kotlinx.coroutines.delay(800)
            // Fermer automatiquement l'écran et retourner au profil
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Noter ${talentName ?: "le talent"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color(0xFF3B82F6)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E293B)
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section: Votre note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF3B82F6).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A).copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Header avec icône
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Votre évaluation",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Partagez votre expérience",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    
                    // Question principale
                    Text(
                        text = "Comment évaluez-vous ce talent ?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Sélection de la note avec slider vertical et catégories
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.5f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            val categories = listOf(
                                Triple(5, "Excellent", "Très satisfait"),
                                Triple(4, "Très bon", "Satisfait"),
                                Triple(3, "Bon", "Neutre"),
                                Triple(2, "Médiocre", "Insatisfait"),
                                Triple(1, "Mauvais", "Très insatisfait")
                            )
                            
                            categories.forEachIndexed { index, (score, title, subtitle) ->
                                val isSelected = selectedScore == score
                                val emoji = when (score) {
                                    5 -> "😍"
                                    4 -> "😊"
                                    3 -> "🙂"
                                    2 -> "😐"
                                    1 -> "😞"
                                    else -> "😐"
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedScore = score }
                                        .padding(vertical = 14.dp, horizontal = 8.dp)
                                        .background(
                                            color = if (isSelected) {
                                                getScoreColorForRating(score).copy(alpha = 0.1f)
                                            } else {
                                                Color.Transparent
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Texte à gauche
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = title,
                                            fontSize = 15.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) {
                                                getScoreColorForRating(score)
                                            } else {
                                                Color(0xFF9CA3AF)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = subtitle,
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    
                                    // Emoji à droite centré verticalement avec le texte
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 48.dp else 40.dp)
                                            .background(
                                                color = if (isSelected) {
                                                    getScoreColorForRating(score).copy(alpha = 0.2f)
                                                } else {
                                                    Color.Transparent
                                                },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = if (isSelected) 32.sp else 28.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Commentaire
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Commentaire",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE2E8F0)
                        )
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { 
                                if (it.length <= 1000) {
                                    comment = it
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { 
                                Text(
                                    "Décrivez votre expérience avec ce talent...",
                                    color = Color(0xFF64748B)
                                )
                            },
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color(0xFFCBD5E1),
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Optionnel",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${comment.length}/1000",
                                        color = if (comment.length > 1000) Color(0xFFEF4444) else Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        )
                    }
                    
                    // Bouton de sauvegarde avec gradient
                    Button(
                        onClick = {
                            viewModel.createOrUpdateRating(
                                talentId = talentId,
                                missionId = missionId,
                                score = selectedScore,
                                recommended = recommended,
                                comment = comment.takeIf { it.isNotEmpty() },
                                tags = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = selectedScore > 0 && !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            disabledContainerColor = Color(0xFF475569)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (myRating != null) "Mettre à jour l'évaluation" else "Enregistrer l'évaluation",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Message de succès avec animation
                    AnimatedVisibility(
                        visible = saveSuccess,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Évaluation enregistrée avec succès",
                                        color = Color(0xFF10B981),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                if (recommended) {
                                    Text(
                                        text = "✓ Ce talent a été recommandé",
                                        color = Color(0xFF10B981).copy(alpha = 0.9f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    
                    // Message d'erreur avec animation
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        errorMessage?.let { error ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = error,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    color = Color(0xFFEF4444),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // Section: Avis des autres recruteurs
            talentRatings?.let { ratings ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Carte de note moyenne
                    if (ratings.averageScore != null) {
                        AverageRatingCard(
                            averageScore = ratings.averageScore,
                            count = ratings.count,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E293B)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucun avis pour le moment",
                                    fontSize = 14.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                    
                    // Liste des ratings
                    if (ratings.ratings.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E293B)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tous les avis",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${ratings.count} ${if (ratings.count == 1) "avis" else "avis"}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF94A3B8),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ratings.ratings.forEach { rating ->
                                        RatingCard(rating = rating)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Slider vertical interactif pour sélectionner une note de 1 à 5
 */
@Composable
private fun VerticalRatingSlider(
    selectedScore: Int,
    onScoreChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var sliderSize by remember { mutableStateOf(IntSize.Zero) }
    val handleSize = 32.dp
    
    // Calculer la position Y du handle en fonction du score
    val handleYPercent = when (selectedScore) {
        5 -> 0.0f
        4 -> 0.25f
        3 -> 0.5f
        2 -> 0.75f
        1 -> 1.0f
        else -> 0.5f
    }
    
    val handleY = remember(handleYPercent, sliderSize.height) {
        if (sliderSize.height > 0) {
            (handleYPercent * sliderSize.height).toFloat()
        } else {
            0f
        }
    }
    
    // Couleur du slider selon le score
    val scoreColor = if (selectedScore > 0) {
        getScoreColorForRating(selectedScore)
    } else {
        Color(0xFF475569)
    }
    
    Box(
        modifier = modifier.onSizeChanged { sliderSize = it }
    ) {
        // Barre de slider verticale avec gradient
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (selectedScore > 0) {
                            val topColor = when (selectedScore) {
                                5 -> Color(0xFF475569)
                                4 -> Color(0xFF475569)
                                3 -> Color(0xFF475569)
                                2 -> scoreColor.copy(alpha = 0.3f)
                                1 -> scoreColor.copy(alpha = 0.5f)
                                else -> Color(0xFF475569)
                            }
                            listOf(
                                topColor,
                                scoreColor.copy(alpha = if (selectedScore <= 2) 0.7f else 0.3f),
                                scoreColor
                            )
                        } else {
                            listOf(
                                Color(0xFF475569),
                                Color(0xFF475569)
                            )
                        }
                    ),
                    shape = RoundedCornerShape(3.dp)
                )
        )
        
        // Zone de glissement pour le slider (zone invisible pour détecter les clics/glissements)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedScore, sliderSize.height) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (sliderSize.height > 0) {
                                val y = offset.y.coerceIn(0f, sliderSize.height.toFloat())
                                val percent = y / sliderSize.height
                                
                                // Convertir le pourcentage en score (1 à 5)
                                val newScore = when {
                                    percent <= 0.1f -> 5
                                    percent <= 0.35f -> 4
                                    percent <= 0.65f -> 3
                                    percent <= 0.9f -> 2
                                    else -> 1
                                }
                                
                                if (newScore != selectedScore) {
                                    onScoreChanged(newScore)
                                }
                            }
                        },
                        onDragEnd = { },
                        onDrag = { change, dragAmount ->
                            if (sliderSize.height > 0) {
                                val y = change.position.y.coerceIn(0f, sliderSize.height.toFloat())
                                val percent = y / sliderSize.height
                                
                                // Convertir le pourcentage en score (1 à 5)
                                val newScore = when {
                                    percent <= 0.1f -> 5
                                    percent <= 0.35f -> 4
                                    percent <= 0.65f -> 3
                                    percent <= 0.9f -> 2
                                    else -> 1
                                }
                                
                                if (newScore != selectedScore) {
                                    onScoreChanged(newScore)
                                }
                            }
                        }
                    )
                }
                .pointerInput(selectedScore, sliderSize.height) {
                    detectTapGestures { offset ->
                        if (sliderSize.height > 0) {
                            val y = offset.y.coerceIn(0f, sliderSize.height.toFloat())
                            val percent = y / sliderSize.height
                            
                            // Convertir le pourcentage en score (1 à 5)
                            val newScore = when {
                                percent <= 0.1f -> 5
                                percent <= 0.35f -> 4
                                percent <= 0.65f -> 3
                                percent <= 0.9f -> 2
                                else -> 1
                            }
                            
                            if (newScore != selectedScore) {
                                onScoreChanged(newScore)
                            }
                        }
                    }
                }
        )
        
        
        // Handle du slider (cercle glissable)
        if (selectedScore > 0 && sliderSize.height > 0) {
            val handleYInDp = with(density) { handleY.toDp() }
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-12).dp, y = handleYInDp - handleSize / 2)
                    .size(handleSize)
                    .background(
                        color = scoreColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .pointerInput(selectedScore, sliderSize.height) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (sliderSize.height > 0) {
                                    val y = offset.y.coerceIn(0f, sliderSize.height.toFloat())
                                    val percent = y / sliderSize.height
                                    
                                    // Convertir le pourcentage en score (1 à 5)
                                    val newScore = when {
                                        percent <= 0.1f -> 5
                                        percent <= 0.35f -> 4
                                        percent <= 0.65f -> 3
                                        percent <= 0.9f -> 2
                                        else -> 1
                                    }
                                    
                                    if (newScore != selectedScore) {
                                        onScoreChanged(newScore)
                                    }
                                }
                            },
                            onDragEnd = { },
                            onDrag = { change, dragAmount ->
                                if (sliderSize.height > 0) {
                                    val y = change.position.y.coerceIn(0f, sliderSize.height.toFloat())
                                    val percent = y / sliderSize.height
                                    
                                    // Convertir le pourcentage en score (1 à 5)
                                    val newScore = when {
                                        percent <= 0.1f -> 5
                                        percent <= 0.35f -> 4
                                        percent <= 0.65f -> 3
                                        percent <= 0.9f -> 2
                                        else -> 1
                                    }
                                    
                                    if (newScore != selectedScore) {
                                        onScoreChanged(newScore)
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Point blanc au centre
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}

private fun getScoreColorForRating(score: Int): Color {
    return when (score) {
        5 -> Color(0xFF10B981) // Vert
        4 -> Color(0xFF3B82F6) // Bleu
        3 -> Color(0xFFF59E0B) // Orange
        2 -> Color(0xFFEF4444) // Rouge
        1 -> Color(0xFFDC2626) // Rouge foncé
        else -> Color(0xFF6B7280) // Gris
    }
}

