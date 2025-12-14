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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
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
import com.example.matchify.ui.skills.SkillPickerView
import com.example.matchify.domain.model.Skill

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
    val skills: List<Skill> by viewModel.skills.collectAsState()
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

    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val whiteText = Color(0xFFFFFFFF)
    val grayText = Color(0xFF9CA3AF)
    val blueButton = Color(0xFF2563EB)

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = darkBackground,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Edit Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White
                    )
                    // Empty box to balance the row
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Avatar Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    val currentUrl by viewModel.currentProfileImageUrl.collectAsState()
                    val modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, cardBackground, CircleShape)

                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = modifier,
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.avatar),
                            placeholder = painterResource(id = R.drawable.avatar)
                        )
                    } else if (!currentUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = currentUrl,
                            contentDescription = null,
                            modifier = modifier,
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.avatar),
                            placeholder = painterResource(id = R.drawable.avatar)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Default Avatar",
                            modifier = modifier,
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Edit icon overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(blueButton)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Change photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Personal Info Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Personal Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = whiteText
                )

                DarkTextField(
                    value = viewModel.fullName.collectAsState().value,
                    onValueChange = { viewModel.fullName.value = it },
                    placeholder = "Full Name",
                    leadingIcon = Icons.Filled.Person
                )

                DarkTextField(
                    value = viewModel.email.collectAsState().value,
                    onValueChange = { viewModel.email.value = it },
                    placeholder = "Email",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email
                )

                DarkTextField(
                    value = viewModel.phone.collectAsState().value,
                    onValueChange = { viewModel.phone.value = it },
                    placeholder = "Phone",
                    leadingIcon = Icons.Filled.Phone,
                    keyboardType = KeyboardType.Phone
                )

                DarkTextField(
                    value = viewModel.location.collectAsState().value,
                    onValueChange = { viewModel.location.value = it },
                    placeholder = "Location",
                    leadingIcon = Icons.Filled.LocationOn
                )
            }

            // Talents Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Talents",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = whiteText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DarkTextField(
                        value = talentInput,
                        onValueChange = { viewModel.talentInput.value = it },
                        placeholder = "Add talent (e.g. Developer)",
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.addTalent() },
                                enabled = talentInput.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = "Add",
                                    tint = if (talentInput.isNotEmpty()) blueButton else grayText
                                )
                            }
                        }
                    )
                }

                if (talents.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(talents) { talent ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = cardBackground
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
                                        color = whiteText
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clickable { viewModel.removeTalent(talent) },
                                        tint = grayText
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Description Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = whiteText
                )

                DarkTextField(
                    value = viewModel.description.collectAsState().value,
                    onValueChange = { viewModel.description.value = it },
                    placeholder = "Write something about yourself...",
                    leadingIcon = Icons.Rounded.Description,
                    singleLine = false,
                    minLines = 4,
                    maxLines = 8
                )
            }



            // Skills Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Skills",
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600),
                    color = whiteText
                )

                val mutableSkills = remember(skills) { 
                    skills.toMutableList() 
                }

                SkillPickerView(
                    selectedSkills = mutableSkills,
                    onSkillsChanged = { updated ->
                        viewModel.updateSelectedSkills(updated)
                    }
                )
            }

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = Color(0xFFEF4444), // Red
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = { viewModel.submit() },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueButton,
                    disabledContainerColor = blueButton.copy(alpha = 0.5f)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { 
            Text(
                text = placeholder, 
                color = Color(0xFF9CA3AF)
            ) 
        },
        leadingIcon = leadingIcon?.let {
            { Icon(it, contentDescription = null, tint = Color(0xFF9CA3AF)) }
        },
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF1E293B),
            unfocusedContainerColor = Color(0xFF1E293B),
            disabledContainerColor = Color(0xFF1E293B),
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color(0xFF334155),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF3B82F6)
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    )
}
