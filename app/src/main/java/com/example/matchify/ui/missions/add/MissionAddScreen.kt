package com.example.matchify.ui.missions.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
    val isSaving by viewModel.isSaving.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            viewModel.onSaveSuccessHandled()
            onMissionCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Mission") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Title") },
                placeholder = { Text("Mission title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Description") },
                placeholder = { Text("Mission description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Duration
            OutlinedTextField(
                value = duration,
                onValueChange = { viewModel.duration.value = it },
                label = { Text("Duration") },
                placeholder = { Text("e.g., 6 mois") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Budget
            OutlinedTextField(
                value = budget,
                onValueChange = { 
                    // Filter out non-numeric characters
                    viewModel.budget.value = it.filter { char -> char.isDigit() }
                },
                label = { Text("Budget") },
                placeholder = { Text("Budget in euros") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Skills Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Skills (max 10)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = skillInput,
                            onValueChange = { viewModel.skillInput.value = it },
                            placeholder = { Text("Add a skill") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.addSkill() },
                                    enabled = skillInput.isNotEmpty() && skills.size < 10
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add"
                                    )
                                }
                            }
                        )
                    }

                    if (skills.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(skills) { skill ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Gray.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 8.dp
                                        ),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = skill,
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.removeSkill(skill) },
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (skills.size >= 10) {
                        Text(
                            text = "Maximum 10 skills reached",
                            fontSize = 12.sp,
                            color = Color(0xFFFF9500)
                        )
                    }
                }
            }

            // Error Message
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Save Button
            Button(
                onClick = { viewModel.createMission() },
                enabled = viewModel.isFormValid && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Save Mission",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

