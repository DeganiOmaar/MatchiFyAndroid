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
import androidx.compose.material.icons.rounded.Add
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
import com.example.matchify.ui.components.MD3OutlinedTextField

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
                title = { 
                    Text(
                        "New Mission",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Back",
                            tint = Color(0xFF007AFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF2F2F2)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Title - MD3 Outlined Text Field
            MD3OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = "Mission title",
                placeholder = "Mission title",
                leadingIcon = Icons.Rounded.Description,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                errorText = null,
                singleLine = true
            )

            // Description - MD3 Outlined Text Field (multi-line)
            MD3OutlinedTextField(
                value = description,
                onValueChange = { viewModel.description.value = it },
                label = "Mission description",
                placeholder = "Mission description",
                leadingIcon = Icons.Rounded.Description,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                errorText = null,
                singleLine = false,
                maxLines = 5
            )

            // Duration - MD3 Outlined Text Field
            MD3OutlinedTextField(
                value = duration,
                onValueChange = { viewModel.duration.value = it },
                label = "Duration",
                placeholder = "e.g., 6 mois",
                leadingIcon = Icons.Rounded.Schedule,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                errorText = null,
                singleLine = true
            )

            // Budget - MD3 Outlined Text Field
            MD3OutlinedTextField(
                value = budget,
                onValueChange = { 
                    // Filter out non-numeric characters
                    viewModel.budget.value = it.filter { char -> char.isDigit() }
                },
                label = "Budget",
                placeholder = "Budget in euros",
                leadingIcon = Icons.Rounded.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                errorText = null,
                singleLine = true
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skills (max 10)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (skills.isEmpty()) {
                            Text(
                                text = "Add at least one",
                                fontSize = 12.sp,
                                color = Color.Red.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = skillInput,
                            onValueChange = { viewModel.skillInput.value = it },
                            placeholder = { Text("Add a skill") },
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(35.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.addSkill() },
                                    enabled = skillInput.isNotEmpty() && skills.size < 10
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Add",
                                        tint = if (skillInput.isNotEmpty() && skills.size < 10) Color(0xFF007AFF) else Color.Gray
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFCCCCCC),
                                unfocusedBorderColor = Color(0xFFDDDDDD)
                            )
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
                                            imageVector = Icons.Rounded.Close,
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

            // Save Button
            Button(
                onClick = { viewModel.createMission() },
                enabled = viewModel.isFormValid && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    disabledContainerColor = Color(0xFFE0E0E0) // More obvious disabled state
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Mission", color = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


