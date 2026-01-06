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
import androidx.compose.material.icons.filled.Cancel
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
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.InterviewRepository
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
                ApiService.getInstance().interviewApi,
                AuthPreferencesProvider.getInstance().get()
            )
        )
    )
) {
    val interviews by viewModel.interviews.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    
    // Filtrer les interviews annulées - elles ne doivent pas apparaître dans la liste
    val upcomingInterviews = interviews.filter { 
        it.isUpcoming && it.status == InterviewStatus.SCHEDULED 
    }
    val pastInterviews = interviews.filter { 
        (it.isPast || it.status == InterviewStatus.COMPLETED) && it.status != InterviewStatus.CANCELLED
    }
    
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
                                    onCancelClick = if (viewModel.isRecruiter && interview.status == InterviewStatus.SCHEDULED) {
                                        { interviewId ->
                                            // Le dialog sera géré dans InterviewCard
                                        }
                                    } else null,
                                    onClick = { onInterviewClick(interview.interviewId) },
                                    isRecruiter = viewModel.isRecruiter,
                                    viewModel = viewModel
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
                                    onJoinClick = {
                                        // Permettre de rejoindre même les interviews passées (pour revoir l'enregistrement)
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(interview.meetLink))
                                        context.startActivity(intent)
                                    },
                                    onCancelClick = if (viewModel.isRecruiter && interview.status == InterviewStatus.SCHEDULED) {
                                        { interviewId ->
                                            // Le dialog sera géré dans InterviewCard
                                        }
                                    } else null,
                                    onClick = { onInterviewClick(interview.interviewId) },
                                    isRecruiter = viewModel.isRecruiter,
                                    viewModel = viewModel
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
    onCancelClick: ((String) -> Unit)?,
    onClick: () -> Unit,
    isRecruiter: Boolean = false,
    viewModel: InterviewsViewModel? = null
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancellationReason by remember { mutableStateOf("") }
    val isLoading by viewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }
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
                    if (isRecruiter) {
                        // Afficher le nom et l'email du talent
                        Column {
                            val talentName = interview.talent?.fullName ?: interview.talentName
                            val talentEmail = interview.talent?.email ?: interview.talentEmail
                            
                            if (!talentName.isNullOrBlank()) {
                                Text(
                                    text = talentName,
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            }
                            // Afficher l'email du talent
                            if (!talentEmail.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = talentEmail,
                                    color = Color(0xFF9CA3AF).copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        // Pour les talents, afficher le nom du recruteur
                        Text(
                            text = interview.recruiterName ?: "Recruteur",
                            color = Color(0xFF9CA3AF),
                            fontSize = 14.sp
                        )
                    }
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
            
            // Afficher la source (Zoom/Google Meet/Manuel)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (interview.source) {
                        com.example.matchify.domain.model.InterviewSource.ZOOM -> Color(0xFF2D8CFF).copy(alpha = 0.2f)
                        com.example.matchify.domain.model.InterviewSource.GOOGLE -> Color(0xFF10B981).copy(alpha = 0.2f)
                        com.example.matchify.domain.model.InterviewSource.MANUAL -> Color(0xFF9CA3AF).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = interview.source.displayName,
                        fontSize = 11.sp,
                        color = when (interview.source) {
                            com.example.matchify.domain.model.InterviewSource.ZOOM -> Color(0xFF2D8CFF)
                            com.example.matchify.domain.model.InterviewSource.GOOGLE -> Color(0xFF10B981)
                            com.example.matchify.domain.model.InterviewSource.MANUAL -> Color(0xFF9CA3AF)
                        },
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (!interview.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = interview.notes ?: "",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
            
            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Bouton Rejoindre (toujours affiché si onJoinClick est disponible)
                if (onJoinClick != null) {
                    Button(
                        onClick = onJoinClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(if (isRecruiter && interview.status == InterviewStatus.SCHEDULED) 1f else 1f)
                    ) {
                        Icon(
                            Icons.Rounded.VideoCall,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rejoindre")
                    }
                }
                
                // Bouton Annuler (seulement pour les recruteurs et interviews planifiées)
                if (isRecruiter && interview.status == InterviewStatus.SCHEDULED) {
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(if (onJoinClick != null) 1f else 1f)
                    ) {
                        Icon(
                            Icons.Filled.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Annuler")
                    }
                }
            }
        }
    }
    
    // Dialog de confirmation d'annulation
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Annuler l'interview",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Voulez-vous vraiment annuler cette interview ? Une notification contenant la raison de l'annulation sera envoyée au talent.",
                        color = Color(0xFFE2E8F0)
                    )
                    OutlinedTextField(
                        value = cancellationReason,
                        onValueChange = { cancellationReason = it },
                        label = { Text("Raison de l'annulation *", color = Color(0xFF9CA3AF)) },
                        placeholder = { Text("Ex: Conflit d'horaire, changement de plan...", color = Color(0xFF6B7280)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cancellationReason.isNotBlank()) {
                            viewModel?.cancelInterview(
                                interviewId = interview.interviewId,
                                cancellationReason = cancellationReason,
                                onSuccess = {
                                    showCancelDialog = false
                                    cancellationReason = ""
                                },
                                onError = { error ->
                                    // L'erreur sera gérée par le ViewModel
                                }
                            )
                        }
                    },
                    enabled = cancellationReason.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Confirmer l'annulation")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        cancellationReason = ""
                    }
                ) {
                    Text("Annuler", color = Color(0xFF9CA3AF))
                }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFE2E8F0)
        )
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

