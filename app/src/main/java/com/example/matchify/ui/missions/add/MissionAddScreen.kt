package com.example.matchify.ui.missions.add

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionAddScreen(
    onBack: () -> Unit,
    onMissionCreated: () -> Unit,
    viewModel: MissionAddViewModel = viewModel(factory = MissionAddViewModelFactory())
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
    val fieldErrors by viewModel.fieldErrors.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    val experienceLevels = listOf(
        "ENTRY" to "Débutant",
        "INTERMEDIATE" to "Intermédiaire",
        "EXPERT" to "Expert"
    )

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.onSaveSuccessHandled()
            onMissionCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Create Mission",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
                
                // Title Section
                val titleError = fieldErrors["title"]
                ModernInputCard(
                    title = "Mission Title",
                    subtitle = titleError
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { viewModel.title.value = it },
                        placeholder = { Text("e.g., Développeur Full Stack", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        isError = titleError != null || (title.isEmpty() && errorMessage != null),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = if (titleError != null || (title.isEmpty() && errorMessage != null)) {
                                Color(0xFFEF4444)
                            } else {
                                Color(0xFF334155)
                            },
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        supportingText = if (titleError != null) {
                            { Text(text = titleError, color = Color(0xFFEF4444)) }
                        } else null
                    )
                }

                // Description Section
                val descriptionError = fieldErrors["description"]
                ModernInputCard(
                    title = "Description",
                    subtitle = descriptionError
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.description.value = it },
                        placeholder = { Text("Describe the mission requirements...", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 6,
                        shape = RoundedCornerShape(12.dp),
                        isError = descriptionError != null || (description.isEmpty() && errorMessage != null),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E293B),
                            unfocusedContainerColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = if (descriptionError != null || (description.isEmpty() && errorMessage != null)) {
                                Color(0xFFEF4444)
                            } else {
                                Color(0xFF334155)
                            },
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        supportingText = if (descriptionError != null) {
                            { Text(text = descriptionError, color = Color(0xFFEF4444)) }
                        } else null
                    )
                }

                // Duration and Budget Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Duration
                    val durationError = fieldErrors["duration"]
                    ModernInputCard(
                        title = "Duration",
                        subtitle = durationError,
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { viewModel.duration.value = it },
                            placeholder = { Text("6 mois", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            isError = durationError != null || (duration.isEmpty() && errorMessage != null),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (durationError != null || (duration.isEmpty() && errorMessage != null)) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                }
                            ),
                            supportingText = if (durationError != null) {
                                { Text(text = durationError, color = MaterialTheme.colorScheme.error) }
                            } else null
                        )
                    }

                    // Budget
                    val budgetError = fieldErrors["budget"]
                    ModernInputCard(
                        title = "Budget",
                        subtitle = budgetError,
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = budget,
                            onValueChange = { 
                                viewModel.budget.value = it.filter { char -> char.isDigit() }
                            },
                            placeholder = { Text("5000", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            isError = budgetError != null || (budget.isEmpty() && errorMessage != null),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = if (budgetError != null || (budget.isEmpty() && errorMessage != null)) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                }
                            ),
                            supportingText = if (budgetError != null) {
                                { Text(text = budgetError, color = MaterialTheme.colorScheme.error) }
                            } else null
                        )
                    }
                }

                // Experience Level Section
                val experienceLevelError = fieldErrors["experienceLevel"]
                ModernInputCard(
                    title = "Niveau d'expérience",
                    subtitle = experienceLevelError
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = experienceLevels.find { it.first == experienceLevel }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Sélectionner un niveau", color = Color(0xFF94A3B8)) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            isError = experienceLevelError != null || (experienceLevel == null && errorMessage != null),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B),
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = if (experienceLevelError != null || (experienceLevel == null && errorMessage != null)) {
                                    Color(0xFFEF4444)
                                } else {
                                    Color(0xFF334155)
                                },
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            supportingText = if (experienceLevelError != null) {
                                { Text(text = experienceLevelError, color = Color(0xFFEF4444)) }
                            } else null,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF1E293B))
                        ) {
                            experienceLevels.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, color = Color.White) },
                                    onClick = {
                                        viewModel.experienceLevel.value = value
                                        expanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF1E293B))
                                )
                            }
                        }
                    }
                }

                // Skills Section
                ModernInputCard(
                    title = "Compétences requises",
                    subtitle = when {
                        skills.isEmpty() && errorMessage != null -> "Au moins une compétence requise"
                        skills.isNotEmpty() -> "${skills.size} compétence${if (skills.size > 1) "s" else ""} ajoutée${if (skills.size > 1) "s" else ""}"
                        else -> null
                    }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = skillInput,
                                onValueChange = { viewModel.skillInput.value = it },
                                placeholder = { Text("Add a skill", color = Color(0xFF94A3B8)) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B),
                                    focusedBorderColor = Color(0xFF3B82F6),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.addSkill() },
                                        enabled = skillInput.isNotEmpty()
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (skillInput.isNotEmpty()) {
                                                Color(0xFF3B82F6)
                                            } else {
                                                Color(0xFF1E293B)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add",
                                                tint = if (skillInput.isNotEmpty()) {
                                                    Color.White
                                                } else {
                                                    Color(0xFF94A3B8)
                                                },
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        if (skills.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(skills) { skill ->
                                    SkillChip(
                                        skill = skill,
                                        onRemove = { viewModel.removeSkill(skill) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Error Message
                errorMessage?.let { error ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                Button(
                    onClick = { viewModel.createMission() },
                    enabled = viewModel.isFormValid && !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        disabledContainerColor = Color(0xFF1E293B),
                        contentColor = Color.White,
                        disabledContentColor = Color(0xFF94A3B8)
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            "Créer la mission",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }


@Composable
private fun ModernInputCard(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color(0xFFEF4444)
                )
            }
        }
        content()
    }
}

@Composable
private fun SkillChip(
    skill: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        modifier = Modifier.clickable { onRemove() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = skill,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF94A3B8)
            )
        }
    }
}
