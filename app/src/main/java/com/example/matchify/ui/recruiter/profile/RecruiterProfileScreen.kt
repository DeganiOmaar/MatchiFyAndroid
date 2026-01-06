package com.example.matchify.ui.recruiter.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterProfileScreen(
    viewModel: RecruiterProfileViewModel,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onBack: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showMenuSheet by remember { mutableStateOf(false) }
    
    // Couleurs du thème sombre (identiques à TalentProfileScreen)
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val whiteText = Color(0xFFFFFFFF)
    val grayText = Color(0xFF9CA3AF)
    val lightGrayText = Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            ProfileHeader(
                onBack = onBack,
                onMoreClick = { showMenuSheet = true },
                backgroundColor = darkBackground
            )

            // Profile Avatar Section
            ProfileAvatarSection(
                imageUrl = user?.profileImageUrl,
                name = user?.fullName ?: "Recruiter Name",
                email = user?.email ?: "",
                location = user?.location
            )

            // Bio Section
            if (!user?.description.isNullOrBlank()) {
                BioSection(
                    bio = user?.description ?: ""
                )
            }

            // Contact Info Section
            ContactInfoSection(
                email = user?.email,
                phone = user?.phone,
                location = user?.location
            )
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(40.dp))
        }
        
        // Error message overlay
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = whiteText
                        )
                        Text(
                            text = errorMessage ?: "",
                            fontSize = 14.sp,
                            color = grayText
                        )
                        Button(
                            onClick = { viewModel.refreshProfile() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    // More Menu Bottom Sheet
    if (showMenuSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = Color(0xFF1E293B),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                )
            }
        ) {
            MenuBottomSheetContent(
                onEditProfile = {
                    showMenuSheet = false
                    onEditProfile()
                },
                onSettings = {
                    showMenuSheet = false
                    onSettings()
                }
            )
        }
    }
}

/**
 * Header avec back arrow, titre centré et menu icon
 */
@Composable
private fun ProfileHeader(
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    backgroundColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back arrow
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Centered title
            Text(
                text = "Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )

            // More menu icon
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Profile Avatar Section avec image circulaire, nom, email et location
 */
@Composable
private fun ProfileAvatarSection(
    imageUrl: String?,
    name: String,
    email: String,
    location: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar circulaire 120dp
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        ) {
            if (imageUrl != null && imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.avatar),
                    placeholder = painterResource(id = R.drawable.avatar)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Default Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Name
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight(700),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Email
        Text(
            text = email,
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Location
        if (!location.isNullOrBlank()) {
            Text(
                text = location,
                fontSize = 14.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Bio Section
 */
@Composable
private fun BioSection(
    bio: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
    ) {
        // Section title
        Text(
            text = "Bio",
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Bio text
        Text(
            text = bio,
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFFE5E7EB),
            lineHeight = 20.sp
        )
    }
}

/**
 * Contact Info Section
 */
@Composable
private fun ContactInfoSection(
    email: String?,
    phone: String?,
    location: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
    ) {
        // Section title
        Text(
            text = "Contact Info",
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Email
        if (!email.isNullOrBlank()) {
            ContactInfoRow(
                icon = Icons.Default.Email,
                text = email
            )
        }

        // Phone
        if (!phone.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            ContactInfoRow(
                icon = Icons.Default.Phone,
                text = phone
            )
        }

        // Location
        if (!location.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            ContactInfoRow(
                icon = Icons.Default.LocationOn,
                text = location
            )
        }
    }
}

/**
 * Contact Info Row
 */
@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF3B82F6)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFFE5E7EB)
        )
    }
}

/**
 * Menu Bottom Sheet Content
 */
@Composable
private fun MenuBottomSheetContent(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, top = 8.dp)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MenuItem(
            icon = Icons.Default.Description,
            title = "Edit Profile",
            onClick = onEditProfile,
            iconColor = Color.White
        )
    }
}

@Composable
private fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
