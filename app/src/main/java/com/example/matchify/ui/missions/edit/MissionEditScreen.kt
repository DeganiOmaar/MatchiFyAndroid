package com.example.matchify.ui.missions.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Mission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionEditScreen(
    mission: Mission,
    onBack: () -> Unit,
    onMissionUpdated: () -> Unit,
    viewModel: MissionEditViewModel = viewModel(
        factory = MissionEditViewModelFactory(mission)
    )
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val skillInput by viewModel.skillInput.collectAsState()
    val skills by viewModel.skills.collectAsState()
    val experienceLevel by viewModel.experienceLevel.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.onSaveSuccessHandled()
            onMissionUpdated()
        }
    }
    val pageBackground = MaterialTheme.colorScheme.surfaceContainerHighest

    Scaffold(
        containerColor = pageBackground,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Modifier la mission",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF007AFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = pageBackground,
                    titleContentColor = Color(0xFF111827)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Titre section
                    Text(
                        text = "Modifier la mission",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title
                    FieldLabel(text = "Titre")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.title.value = it },
                        leadingIcon = {
                            Icon(Icons.Rounded.Description, contentDescription = null, tint = Color.Gray)
                        },
                        placeholder = { Text("Titre de la mission") },
                        singleLine = true,
                        shape = RoundedCornerShape(35.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFCCCCCC),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )

                    // Description
                    FieldLabel(text = "Description")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.description.value = it },
                        leadingIcon = {
                            Icon(Icons.Rounded.Description, contentDescription = null, tint = Color.Gray)
                        },
                        placeholder = { Text("Description de la mission") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 6,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFCCCCCC),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )

                    // Durée (liste déroulante comme à la création)
                    FieldLabel(text = "Durée")
                    val durationOptions = listOf(
                        "Moins d'1 mois",
                        "1 à 3 mois",
                        "3 à 6 mois",
                        "Plus de 6 mois"
                    )
                    var durationExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = durationExpanded,
                        onExpandedChange = { durationExpanded = !durationExpanded }
                    ) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Schedule,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            },
                            placeholder = { Text("Durée de la mission") },
                            singleLine = true,
                            readOnly = true,
                            shape = RoundedCornerShape(28.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = durationExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .height(55.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF007AFF),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF9FAFB)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = durationExpanded,
                            onDismissRequest = { durationExpanded = false }
                        ) {
                            durationOptions.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFF111827)
                                        )
                                    },
                                    onClick = {
                                        viewModel.duration.value = option
                                        durationExpanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(
                                        horizontal = 20.dp,
                                        vertical = 10.dp
                                    )
                                )
                                if (index != durationOptions.lastIndex) {
                                    Divider(color = Color(0xFFE5E7EB))
                                }
                            }
                        }
                    }

                    // Budget
                    FieldLabel(text = "Budget (en euros)")
                    OutlinedTextField(
                        value = budget,
                        onValueChange = {
                            viewModel.budget.value = it.filter { char -> char.isDigit() }
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.AttachMoney, contentDescription = null, tint = Color.Gray)
                        },
                        placeholder = { Text("Budget (en euros)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(35.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFCCCCCC),
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        )
                    )

                    // Niveau d'expérience
                    FieldLabel(text = "Niveau d'expérience")
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExperienceChip(
                            label = "Débutant",
                            selected = experienceLevel == "ENTRY",
                            onClick = { viewModel.experienceLevel.value = "ENTRY" }
                        )
                        ExperienceChip(
                            label = "Intermédiaire",
                            selected = experienceLevel == "INTERMEDIATE",
                            onClick = { viewModel.experienceLevel.value = "INTERMEDIATE" }
                        )
                        ExperienceChip(
                            label = "Expert",
                            selected = experienceLevel == "EXPERT",
                            onClick = { viewModel.experienceLevel.value = "EXPERT" }
                        )
                    }

                    // Skills Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF3F4F6)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FieldLabel(text = "Compétences requises")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = skillInput,
                                    onValueChange = { viewModel.skillInput.value = it },
                                    placeholder = {
                                        Text(
                                            text = "Ajouter une compétence",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    singleLine = true,
                                    shape = RoundedCornerShape(26.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFE5E7EB),
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        focusedContainerColor = Color(0xFFF9FAFB),
                                        unfocusedContainerColor = Color(0xFFF9FAFB)
                                    )
                                )

                                if (skillInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.addSkill() },
                                        enabled = true,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = "Ajouter une compétence",
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF2563EB)
                                        )
                                    }
                                } else {
                                    // Espace réservé pour alignement, sans icône
                                    Spacer(modifier = Modifier.size(32.dp))
                                }
                            }

                            if (skills.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(skills) { skill ->
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFE5E7EB)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                ),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = skill,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF111827)
                                                )
                                                Icon(
                                                    imageVector = Icons.Rounded.Close,
                                                    contentDescription = "Supprimer",
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clickable { viewModel.removeSkill(skill) },
                                                    tint = Color(0xFF6B7280)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Save Button
                    Button(
                        onClick = { viewModel.updateMission() },
                        enabled = viewModel.isFormValid && !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF),
                            disabledContainerColor = Color(0xFFBAD7FF)
                        )
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enregistrer les modifications", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
private fun FieldLabel(text: String) {
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp
    )

    Text(
        text = text.uppercase(),
        style = labelStyle,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
private fun ExperienceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0xFFEFF6FF) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) Color(0xFF2563EB) else Color(0xFFE5E7EB)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = when (label) {
                        "Débutant" -> "Peu expérimenté"
                        "Intermédiaire" -> "Expérience modérée"
                        else -> "Très expérimenté"
                    },
                    fontSize = 11.sp,
                    color = if (selected) Color(0xFF2563EB) else Color(0xFF6B7280)
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color(0xFF2563EB)
                )
            }
        }
    }
}
