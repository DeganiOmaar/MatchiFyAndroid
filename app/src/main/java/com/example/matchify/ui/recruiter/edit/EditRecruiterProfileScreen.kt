package com.example.matchify.ui.recruiter.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import java.io.File
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecruiterProfileScreen(
    viewModel: EditRecruiterProfileViewModel,
    onBack: () -> Unit
) {
    val isSaving by viewModel.saving.collectAsState()
    val isSaved by viewModel.saved.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()

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
                    // Avatar image
                    AsyncImage(
                        model = selectedImageUri ?: viewModel.currentProfileImageUrl.collectAsState().value,
                        contentDescription = null,
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray.copy(alpha = 0.2f), CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.avatar),
                        placeholder = painterResource(id = R.drawable.avatar)
                    )

                    // Edit icon overlay - blue circle with white edit icon
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

            // Save Button - same style as Add Mission
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
