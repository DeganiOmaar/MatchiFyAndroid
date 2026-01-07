package com.example.matchify.ui.talent.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.domain.model.UserModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTalentsScreen(
    onBack: () -> Unit,
    onTalentClick: (String) -> Unit = {},
    viewModel: FavoriteTalentsViewModel = viewModel(
        factory = FavoriteTalentsViewModelFactory()
    )
) {
    val favoriteTalents by viewModel.favoriteTalents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var talentToDelete by remember { mutableStateOf<String?>(null) }
    
    // Charger les favoris au démarrage et quand on revient sur l'écran
    LaunchedEffect(Unit) {
        viewModel.loadFavoriteTalents()
    }
    
    // Recharger quand on revient sur l'écran (DisposableEffect)
    DisposableEffect(Unit) {
        viewModel.loadFavoriteTalents()
        onDispose { }
    }
    
    // Couleurs du thème sombre
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val whiteText = Color(0xFFFFFFFF)
    val grayText = Color(0xFF94A3B8)
    
    Scaffold(
        topBar = {
            com.example.matchify.ui.components.MatchifyTopAppBar(
                title = "Talents Favoris",
                onBack = onBack
            )
        },
        containerColor = darkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF3B82F6)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Erreur",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "Une erreur est survenue",
                            fontSize = 14.sp,
                            color = grayText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadFavoriteTalents() }) {
                            Text("Réessayer")
                        }
                    }
                }
                favoriteTalents.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = grayText
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aucun talent favori",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = whiteText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Les talents que vous ajoutez aux favoris apparaîtront ici",
                            fontSize = 14.sp,
                            color = grayText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoriteTalents) { talent ->
                            TalentFavoriteCard(
                                talent = talent,
                                onTalentClick = { onTalentClick(talent.id ?: "") },
                                onDeleteClick = {
                                    talentToDelete = talent.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Dialog de confirmation de suppression
    talentToDelete?.let { talentId ->
        AlertDialog(
            onDismissRequest = { talentToDelete = null },
            title = {
                Text(
                    text = "Supprimer des favoris",
                    fontWeight = FontWeight.Bold,
                    color = whiteText
                )
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir retirer ce talent de vos favoris ?",
                    color = grayText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeFavoriteTalent(talentId)
                        talentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { talentToDelete = null }
                ) {
                    Text("Annuler", color = grayText)
                }
            },
            containerColor = cardBackground,
            titleContentColor = whiteText,
            textContentColor = grayText
        )
    }
}

@Composable
private fun TalentFavoriteCard(
    talent: UserModel,
    onTalentClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardBackground = Color(0xFF1E293B)
    val whiteText = Color(0xFFFFFFFF)
    val grayText = Color(0xFF94A3B8)
    val profileImageBg = Color(0xFFF5F5DC)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTalentClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Profile Image
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = profileImageBg
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (talent.profileImageUrl != null) {
                            AsyncImage(
                                model = talent.profileImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                }
                
                // Talent Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = talent.fullName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = whiteText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (talent.talent?.isNotEmpty() == true) {
                        Text(
                            text = talent.talent.joinToString(", "),
                            fontSize = 14.sp,
                            color = grayText
                        )
                    }
                    if (talent.location != null) {
                        Text(
                            text = talent.location,
                            fontSize = 12.sp,
                            color = grayText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer des favoris",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

