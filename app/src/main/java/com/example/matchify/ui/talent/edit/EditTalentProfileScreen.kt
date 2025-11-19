package com.example.matchify.ui.talent.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTalentProfileScreen(
    viewModel: EditTalentProfileViewModel,
    onBack: () -> Unit
) {
    val isSaving by viewModel.saving.collectAsState()
    val isSaved by viewModel.saved.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val talents by viewModel.talents.collectAsState()
    val talentInput by viewModel.talentInput.collectAsState()
    val skills by viewModel.skills.collectAsState()
    val skillInput by viewModel.skillInput.collectAsState()

    // Handle navigation back after save
    LaunchedEffect(isSaved) {
        if (isSaved) {
            onBack()
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setSelectedImageUri(it)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1A1A1A)
                            )
                        }
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    // Avatar image - priority: selectedImageUri > currentProfileImageUrl > default avatar
                    val currentUrl by viewModel.currentProfileImageUrl.collectAsState()
                    
                    when {
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray.copy(alpha = 0.2f), CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.avatar),
                                placeholder = painterResource(id = R.drawable.avatar)
                            )
                        }
                        !currentUrl.isNullOrBlank() -> {
                            AsyncImage(
                                model = currentUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray.copy(alpha = 0.2f), CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.avatar),
                                placeholder = painterResource(id = R.drawable.avatar)
                            )
                        }
                        else -> {
                            Image(
                                painter = painterResource(id = R.drawable.avatar),
                                contentDescription = "Default Avatar",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.Gray.copy(alpha = 0.2f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Edit icon overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF007AFF))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Personal Info Section Header
            Text(
                text = "Personal Info",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Full Name
            OutlinedTextField(
                value = viewModel.fullName.collectAsState().value,
                onValueChange = { viewModel.fullName.value = it },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true,
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Email
            OutlinedTextField(
                value = viewModel.email.collectAsState().value,
                onValueChange = { viewModel.email.value = it },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Phone
            OutlinedTextField(
                value = viewModel.phone.collectAsState().value,
                onValueChange = { viewModel.phone.value = it },
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Location
            OutlinedTextField(
                value = viewModel.location.collectAsState().value,
                onValueChange = { viewModel.location.value = it },
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true,
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Talents Section
            Text(
                text = "Talents",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = talentInput,
                    onValueChange = { viewModel.talentInput.value = it },
                    placeholder = { Text("Add talent (e.g. Developer, Photographer)") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.addTalent() },
                            enabled = talentInput.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add",
                                tint = if (talentInput.isNotEmpty()) Color(0xFF007AFF) else Color.Gray
                            )
                        }
                    },
                    shape = RoundedCornerShape(35.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFCCCCCC),
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    )
                )
            }

            if (talents.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(talents) { talent ->
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
                                    text = talent,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                                IconButton(
                                    onClick = { viewModel.removeTalent(talent) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove",
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = viewModel.description.collectAsState().value,
                onValueChange = { viewModel.description.value = it },
                leadingIcon = {
                    Icon(Icons.Rounded.Description, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 6,
                minLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            // Skills Section
            Text(
                text = "Skills",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = skillInput,
                    onValueChange = { viewModel.skillInput.value = it },
                    placeholder = { Text("Add skill") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.addSkill() },
                            enabled = skillInput.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Add",
                                tint = if (skillInput.isNotEmpty()) Color(0xFF007AFF) else Color.Gray
                            )
                        }
                    },
                    shape = RoundedCornerShape(35.dp),
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

            // Portfolio Link
            OutlinedTextField(
                value = viewModel.portfolioLink.collectAsState().value,
                onValueChange = { viewModel.portfolioLink.value = it },
                leadingIcon = {
                    Icon(Icons.Filled.Link, contentDescription = null, tint = Color.Gray)
                },
                placeholder = { Text("Portfolio Link (URL)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri
                ),
                shape = RoundedCornerShape(35.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFCCCCCC),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Save Button
            Button(
                onClick = { viewModel.submit() },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
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
                    Text(
                        text = "Save Changes",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

