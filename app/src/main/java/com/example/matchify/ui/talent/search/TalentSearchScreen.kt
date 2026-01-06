package com.example.matchify.ui.talent.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.domain.model.Talent
import kotlinx.coroutines.launch


import com.example.matchify.ui.talent.search.components.TalentSearchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentSearchScreen(
    onBack: () -> Unit,
    onTalentClick: (String) -> Unit
) {
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val talentRepository = remember {
        val apiService = ApiService.getInstance()
        TalentRepository(apiService.talentApi, prefs)
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var filteredTalents by remember { mutableStateOf<List<Talent>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val darkBackground = Color(0xFF0F172A)
    
    // Rechercher les talents uniquement quand l'utilisateur tape dans la barre de recherche
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            isLoading = true
            scope.launch {
                try {
                    // Note: arguments limit/page removed as they are not supported by the stubbed repository yet
                    val allTalents = talentRepository.getAllTalents()
                    filteredTalents = allTalents.filter { talent ->
                        talent.fullName?.contains(searchQuery, ignoreCase = true) == true ||
                        talent.email?.contains(searchQuery, ignoreCase = true) == true ||
                        talent.skills?.any { skill -> 
                            skill.contains(searchQuery, ignoreCase = true) 
                        } == true ||
                        talent.description?.contains(searchQuery, ignoreCase = true) == true
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TalentSearchScreen", "Error searching talents: ${e.message}", e)
                    filteredTalents = emptyList()
                } finally {
                    isLoading = false
                }
            }
        } else {
            filteredTalents = emptyList()
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = darkBackground
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Back button à gauche
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Titre centré
                    Text(
                        text = "Rechercher des talents",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        },
        containerColor = darkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barre de recherche unique
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(
                        text = "Rechercher",
                        color = Color(0xFFE2E8F0),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(24.dp)
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFF64748B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF3B82F6),
                    unfocusedPlaceholderColor = Color(0xFFE2E8F0),
                    focusedPlaceholderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Liste des talents
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3B82F6))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTalents) { talent ->
                        TalentSearchCard(
                            talent = talent,
                            onClick = { onTalentClick(talent.talentId) }
                        )
                    }
                    
                    if (filteredTalents.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Aucun talent trouvé",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



