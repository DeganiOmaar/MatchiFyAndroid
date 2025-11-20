package com.example.matchify.ui.missions.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    var currentStep by remember { mutableStateOf(0) } // 0 = À propos, 1 = Exigences, 2 = Budget

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.onSaveSuccessHandled()
            onMissionCreated()
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
                            "Nouvelle mission",
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
            Spacer(modifier = Modifier.height(8.dp))

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
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MissionAddStepHeader(currentStep = currentStep)

                    when (currentStep) {
                        0 -> {
                            // Étape 1 : À propos de la mission (titre + description)
                            Spacer(modifier = Modifier.height(4.dp))

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
                        }

                        1 -> {
                            // Étape 2 : Exigences (durée + expérience + compétences)
                            Text(
                                text = "Exigences du freelance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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

                            Spacer(modifier = Modifier.height(6.dp))

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
                                            shape = RoundedCornerShape(24.dp),
                                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFFE5E7EB),
                                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                                focusedContainerColor = Color(0xFFF9FAFB),
                                                unfocusedContainerColor = Color(0xFFF9FAFB),
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                            )
                                        )

                                        IconButton(
                                            onClick = { viewModel.addSkill() },
                                            enabled = skillInput.isNotEmpty(),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Add,
                                                contentDescription = "Ajouter une compétence",
                                                modifier = Modifier.size(18.dp),
                                                tint = if (skillInput.isNotEmpty())
                                                    Color(0xFF2563EB)
                                                else
                                                    Color(0xFF9CA3AF)
                                            )
                                        }
                                    }

                                    if (skills.isNotEmpty()) {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            itemsIndexed(skills) { index, skill ->
                                                val level = index % 3
                                                SkillChip(
                                                    label = skill,
                                                    level = level,
                                                    onRemove = { viewModel.removeSkill(skill) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Étape 3 : Budget

                            Spacer(modifier = Modifier.height(4.dp))

                            FieldLabel(text = "Budget (en euros)")
                            OutlinedTextField(
                                value = budget,
                                onValueChange = {
                                    // garder uniquement les chiffres
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
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Error Message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation des étapes / création
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        if (currentStep == 0) onBack() else currentStep -= 1
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF111827)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFD1D5DB))
                ) {
                    if (currentStep == 0) {
                        Text("Annuler")
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Précédent")
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        when (currentStep) {
                            0 -> {
                                if (title.isBlank() || description.isBlank()) {
                                    viewModel.setInlineError("Veuillez remplir le titre et la description.")
                                } else currentStep = 1
                            }
                            1 -> {
                                if (duration.isBlank() || skills.isEmpty()) {
                                    viewModel.setInlineError("Veuillez renseigner la durée et au moins une compétence.")
                                } else currentStep = 2
                            }
                            2 -> {
                                viewModel.createMission()
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
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
                        val label = when (currentStep) {
                            0, 1 -> "Suivant"
                            else -> "Créer la mission"
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, color = Color.White)
                            if (currentStep != 2) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
private fun MissionAddStepHeader(currentStep: Int) {
    val steps = listOf("À propos", "Exigences", "Budget")

    val label = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Étape ${currentStep + 1}",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF6B7280)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Indicateur simple avec tirets (un actif, les autres inactifs)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .height(3.dp)
                        .width(if (index == currentStep) 18.dp else 12.dp)
                        .background(
                            color = if (index == currentStep) Color(0xFF2563EB) else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
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
        border = BorderStroke(
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

@Composable
private fun SkillChip(
    label: String,
    level: Int,
    onRemove: () -> Unit
) {
    // Choix automatique du style selon le niveau (0,1,2)
    val background: Color
    val borderColor: Color
    val textColor: Color
    val accentIcon: ImageVector?

    when (level) {
        // Niveau 1 – style très léger
        0 -> {
            background = Color.Transparent
            borderColor = Color(0xFFE5E7EB)
            textColor = Color(0xFF111827)
            accentIcon = null
        }
        // Niveau 2 – style intermédiaire
        1 -> {
            background = Color(0xFFEFF6FF)
            borderColor = Color(0xFF2563EB)
            textColor = Color(0xFF111827)
            accentIcon = null
        }
        // Niveau 3 – style avancé
        else -> {
            background = Color.White
            borderColor = Color(0xFF111827)
            textColor = Color(0xFF111827)
            accentIcon = Icons.Rounded.Check
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = background,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            accentIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = borderColor
                )
            }
            Text(
                text = label,
                fontSize = 13.sp,
                color = textColor
            )
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Supprimer",
                modifier = Modifier
                    .size(14.dp)
                    .clickable(onClick = onRemove),
                tint = Color.Gray
            )
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

