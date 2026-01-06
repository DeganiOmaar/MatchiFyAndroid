package com.example.matchify.ui.interviews

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Interview
import com.example.matchify.domain.model.InterviewStatus
import com.example.matchify.ui.components.MatchifyTopAppBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InterviewsListScreen(
    onBack: () -> Unit,
    onInterviewClick: (String) -> Unit = {}, // interviewId
    viewModel: InterviewsViewModel = viewModel(
        factory = InterviewsViewModelFactory(
            InterviewRepository(
                com.example.matchify.data.remote.ApiService.getInstance().interviewApi,
                com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
            )
        )
    )
) {
    val interviews by viewModel.interviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    
    val upcomingInterviews = interviews.filter { it.isUpcoming && it.status == InterviewStatus.SCHEDULED }
    val pastInterviews = interviews.filter { it.isPast || it.status == InterviewStatus.COMPLETED || it.status == InterviewStatus.CANCELLED }
    
    Scaffold(
        topBar = {
            MatchifyTopAppBar(
                title = "Mes interviews",
                onBack = onBack
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
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
                            text = errorMessage ?: "Une erreur est survenue",
                            color = Color(0xFFEF4444),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadInterviews() }) {
                            Text("Réessayer")
                        }
                    }
                }
                interviews.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aucune interview",
                            color = Color(0xFF9CA3AF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vous n'avez pas encore d'interviews planifiées",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (upcomingInterviews.isNotEmpty()) {
                            item {
                                Text(
                                    text = "À venir",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(upcomingInterviews) { interview ->
                                InterviewCard(
                                    interview = interview,
                                    onJoinClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(interview.meetLink))
                                        context.startActivity(intent)
                                    },
                                    onClick = { onInterviewClick(interview.interviewId) }
                                )
                            }
                        }
                        
                        if (pastInterviews.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Passées",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(pastInterviews) { interview ->
                                InterviewCard(
                                    interview = interview,
                                    onJoinClick = null, // Pas de bouton pour les interviews passées
                                    onClick = { onInterviewClick(interview.interviewId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InterviewCard(
    interview: Interview,
    onJoinClick: (() -> Unit)?,
    onClick: () -> Unit,
    isRecruiter: Boolean = false
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.FRENCH)
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = interview.missionTitle ?: "Mission",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRecruiter) {
                            interview.talentName ?: "Talent"
                        } else {
                            interview.recruiterName ?: "Recruteur"
                        },
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                }
                
                StatusBadge(status = interview.status)
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Rounded.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF3B82F6)
                )
                Text(
                    text = interview.formattedScheduledDate,
                    color = Color(0xFFE2E8F0),
                    fontSize = 14.sp
                )
            }
            
            if (!interview.notes.isNullOrBlank()) {
                Text(
                    text = interview.notes ?: "",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
            
            if (onJoinClick != null && interview.status == InterviewStatus.SCHEDULED) {
                Button(
                    onClick = onJoinClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Rounded.VideoCall,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rejoindre Meet")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: InterviewStatus) {
    val (text, bgColor, textColor) = when (status) {
        InterviewStatus.SCHEDULED -> Triple("Planifié", Color(0xFF3B82F6), Color(0xFFDBEAFE))
        InterviewStatus.COMPLETED -> Triple("Terminé", Color(0xFF10B981), Color(0xFFD1FAE5))
        InterviewStatus.CANCELLED -> Triple("Annulé", Color(0xFFEF4444), Color(0xFFFEE2E2))
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

