package com.example.matchify.ui.interviews

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Schedule
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
import com.example.matchify.ui.components.MatchifyTopAppBar
import com.example.matchify.ui.talent.edit.DarkTextField
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateInterviewScreen(
    proposalId: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CreateInterviewViewModel = viewModel(
        factory = CreateInterviewViewModelFactory(proposalId)
    )
) {
    val context = LocalContext.current
    val scheduledDate = viewModel.scheduledDate.collectAsState().value
    val notes = viewModel.notes.collectAsState().value
    val meetLink = viewModel.meetLink.collectAsState().value
    val useAutoGenerate = viewModel.useAutoGenerate.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.FRENCH)
    
    val success = viewModel.success.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    
    LaunchedEffect(success) {
        if (success) {
            // Afficher un message de succès selon le mode utilisé
            val message = if (useAutoGenerate) {
                "✓ Interview planifiée ! Le lien Zoom/Meet a été généré automatiquement et envoyé au talent par email."
            } else {
                "✓ Interview planifiée ! Le lien de réunion a été envoyé au talent par email."
            }
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long
                )
                // Attendre un peu pour que l'utilisateur voie le message
                kotlinx.coroutines.delay(2500)
                onSuccess()
                onBack()
            }
        }
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    )
                }
            )
        },
        topBar = {
            MatchifyTopAppBar(
                title = "Planifier une interview",
                onBack = onBack
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date & Time Selection
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Date et heure *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9CA3AF)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Date Button
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                scheduledDate?.let {
                                    calendar.time = it
                                }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val selectedDate = Calendar.getInstance().apply {
                                            set(year, month, dayOfMonth)
                                            // Si on a déjà une heure sélectionnée, la conserver
                                            scheduledDate?.let { existingDate ->
                                                val existingCal = Calendar.getInstance()
                                                existingCal.time = existingDate
                                                set(Calendar.HOUR_OF_DAY, existingCal.get(Calendar.HOUR_OF_DAY))
                                                set(Calendar.MINUTE, existingCal.get(Calendar.MINUTE))
                                            } ?: run {
                                                // Sinon, utiliser l'heure actuelle
                                                val now = Calendar.getInstance()
                                                set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
                                                set(Calendar.MINUTE, now.get(Calendar.MINUTE))
                                            }
                                        }
                                        viewModel.setScheduledDate(selectedDate.time)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (scheduledDate != null) Color(0xFF3B82F6) else Color(0xFF111827),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Schedule, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = scheduledDate?.let { dateFormat.format(it) } ?: "Sélectionner date",
                                fontSize = 14.sp
                            )
                        }
                        
                        // Time Button
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                scheduledDate?.let {
                                    calendar.time = it
                                } ?: run {
                                    // Si aucune date n'est sélectionnée, utiliser la date d'aujourd'hui
                                    calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                                    calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                                    calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                                }
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        val selectedDate = scheduledDate?.let {
                                            val cal = Calendar.getInstance()
                                            cal.time = it
                                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            cal.set(Calendar.MINUTE, minute)
                                            cal.time
                                        } ?: Calendar.getInstance().apply {
                                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                                            set(Calendar.MINUTE, minute)
                                        }.time
                                        viewModel.setScheduledDate(selectedDate)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (scheduledDate != null) Color(0xFF3B82F6) else Color(0xFF111827),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = scheduledDate?.let { timeFormat.format(it) } ?: "Heure",
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    // Affichage de la date/heure sélectionnée
                    if (scheduledDate != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Sélectionné: ${dateFormat.format(scheduledDate)} à ${timeFormat.format(scheduledDate)}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF3B82F6),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            // Lien de réunion - Toggle pour choisir entre automatique et manuel
            Surface(
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
                                text = "Lien de réunion",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF9CA3AF)
                            )
                            Text(
                                text = if (useAutoGenerate) {
                                    "Génération automatique"
                                } else {
                                    "Lien manuel"
                                },
                                fontSize = 12.sp,
                                color = Color(0xFF9CA3AF).copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Switch(
                            checked = useAutoGenerate,
                            onCheckedChange = { viewModel.setUseAutoGenerate(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF10B981),
                                checkedTrackColor = Color(0xFF10B981).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color(0xFF9CA3AF),
                                uncheckedTrackColor = Color(0xFF475569)
                            )
                        )
                    }
                    
                    if (useAutoGenerate) {
                        // Information sur la génération automatique
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Le lien Zoom/Meet sera généré automatiquement et envoyé au talent par email",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        // Champ pour saisir le lien manuellement
                        DarkTextField(
                            value = meetLink,
                            onValueChange = { viewModel.setMeetLink(it) },
                            placeholder = "https://zoom.us/j/... ou https://meet.google.com/...",
                            singleLine = true
                        )
                        Text(
                            text = "Le lien saisi sera envoyé au talent par email",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF).copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // Notes (Optional)
            DarkTextField(
                value = notes,
                onValueChange = { viewModel.setNotes(it) },
                placeholder = "Notes (optionnel)",
                minLines = 4
            )
            
            // Error Message
            errorMessage?.let { error ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Create Button
            Button(
                onClick = { viewModel.createInterview() },
                enabled = !isLoading && scheduledDate != null && (useAutoGenerate || meetLink.isNotBlank()),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF1E293B)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Planifier l'interview",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

