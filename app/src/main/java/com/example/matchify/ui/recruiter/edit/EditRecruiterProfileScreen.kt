package com.example.matchify.ui.recruiter.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector

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

    // Couleur de fond sombre (identique aux autres écrans)
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)

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
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
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
                            // Show selected image
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
                            // Show current backend image
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
                            // Show default avatar
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
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Carte contenant les champs, avec fond sombre
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = cardBackground,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Full Name
                    OutlinedTextField(
                        value = viewModel.fullName.collectAsState().value,
                        onValueChange = { viewModel.fullName.value = it },
                        leadingIcon = {
                            LeadingIconCircle(icon = Icons.Filled.Person)
                        },
                        placeholder = { Text("Full Name", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(30.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBackground,
                            unfocusedContainerColor = cardBackground,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Email
                    OutlinedTextField(
                        value = viewModel.email.collectAsState().value,
                        onValueChange = { viewModel.email.value = it },
                        leadingIcon = {
                            LeadingIconCircle(icon = Icons.Filled.Email)
                        },
                        placeholder = { Text("Email", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                    )

                    // Phone
                    OutlinedTextField(
                        value = viewModel.phone.collectAsState().value,
                        onValueChange = { newValue ->
                            // Garder uniquement les chiffres et limiter à 8 caractères
                            val digitsOnly = newValue.filter { it.isDigit() }
                            val limited = digitsOnly.take(8)
                            viewModel.phone.value = limited
                        },
                        leadingIcon = {
                            LeadingIconCircle(icon = Icons.Filled.Phone)
                        },
                        placeholder = { Text("Phone", color = Color(0xFF94A3B8)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone
                        ),
                        shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                    )

                    // Location (sélecteur intelligent de villes de Tunisie)
                    val tunisianCities = listOf(
                        "Tunis",
                        "Ariana",
                        "Ben Arous",
                        "Manouba",
                        "Sfax",
                        "Sousse",
                        "Monastir",
                        "Mahdia",
                        "Nabeul",
                        "Bizerte",
                        "Gabès",
                        "Kairouan",
                        "Gafsa",
                        "Tozeur",
                        "Médenine",
                        "Tataouine",
                        "Kasserine",
                        "Kébili",
                        "Jendouba",
                        "Beja",
                        "Siliana",
                        "Kef",
                        "Zaghouan"
                    )
                    var locationMenuExpanded by remember { mutableStateOf(false) }
                    val currentLocation by viewModel.location.collectAsState()
                    var locationQuery by remember { mutableStateOf(currentLocation) }

                    // Filtrage dynamique en fonction de ce que l'utilisateur tape (début du nom)
                    // Si le champ est vide -> aucune ville proposée (liste masquée)
                    val filteredCities = remember(locationQuery) {
                        if (locationQuery.isBlank()) emptyList()
                        else tunisianCities.filter {
                            it.startsWith(locationQuery.trim(), ignoreCase = true)
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = locationMenuExpanded,
                        onExpandedChange = {
                            // Ne pas ouvrir la liste si aucun texte n'est saisi
                            if (locationQuery.isNotBlank()) {
                                locationMenuExpanded = !locationMenuExpanded
                            }
                        }
                    ) {
                        OutlinedTextField(
                            value = locationQuery,
                            onValueChange = { value ->
                                locationQuery = value
                                viewModel.location.value = value
                                // Ouvrir/fermer automatiquement selon la présence de texte
                                locationMenuExpanded = value.isNotBlank()
                            },
                            leadingIcon = {
                                LeadingIconCircle(icon = Icons.Filled.LocationOn)
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationMenuExpanded)
                            },
                            placeholder = { Text("Choisissez votre ville (Tunisie)", color = Color(0xFF94A3B8)) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .height(52.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = cardBackground,
                                unfocusedContainerColor = cardBackground,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = locationMenuExpanded,
                            onDismissRequest = { locationMenuExpanded = false },
                            modifier = Modifier.background(cardBackground)
                        ) {
                            filteredCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city, color = Color.White) },
                                    onClick = {
                                        locationQuery = city
                                        viewModel.location.value = city
                                        locationMenuExpanded = false
                                    }
                                )
                            }

                            if (filteredCities.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Aucune ville trouvée", color = Color(0xFF94A3B8)) },
                                    onClick = { }
                                )
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
                    Icon(Icons.Rounded.Description, contentDescription = null, tint = Color(0xFF94A3B8))
                },
                placeholder = { Text("Description", color = Color(0xFF94A3B8)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                maxLines = 6,
                minLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = cardBackground,
                    unfocusedContainerColor = cardBackground,
                    focusedBorderColor = Color(0xFF3B82F6),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
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
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF1E293B)
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

@Composable
private fun LeadingIconCircle(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(32.dp),
        shape = CircleShape,
        color = Color(0xFF334155)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
