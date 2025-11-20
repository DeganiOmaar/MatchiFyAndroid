package com.example.matchify.ui.talent.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
fun TalentProfileScreen(
    viewModel: TalentProfileViewModel,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val joined by viewModel.joinedDate.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showMenuSheet by remember { mutableStateOf(false) }

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
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { showMenuSheet = true },
                        shape = CircleShape,
                        color = Color(0xFFF5F7FA)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "Menu",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Error message
                if (errorMessage != null) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Premium Header Section with Gradient
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        // Gradient Background
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        ),
                                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                    )
                                )
                        )
                        
                        // Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Premium Avatar with Glow Effect
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(
                                        elevation = 16.dp,
                                        shape = CircleShape,
                                        spotColor = Color.White.copy(alpha = 0.5f)
                                    )
                            ) {
                                val imageUrl = user?.profileImageUrl
                                if (imageUrl != null && imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 4.dp,
                                                color = Color.White,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop,
                                        error = painterResource(id = R.drawable.avatar),
                                        placeholder = painterResource(id = R.drawable.avatar)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar),
                                        contentDescription = "Default Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(
                                                width = 4.dp,
                                                color = Color.White,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // Name
                            Text(
                                text = user?.fullName ?: "Talent Name",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Email
                            Text(
                                text = user?.email ?: "-",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Joined Date Badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarToday,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Membre depuis $joined",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Information Sections
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // About Section
                        if (!user?.description.isNullOrBlank()) {
                            PremiumAboutSection(
                                description = user?.description ?: ""
                            )
                        }

                        // Talent Category Section
                        if (!user?.skills.isNullOrEmpty()) {
                            PremiumSkillsSection(
                                skills = user?.skills ?: emptyList()
                            )
                        }

                        // Skills Section
                        if (!user?.skills.isNullOrEmpty()) {
                            PremiumSkillsSection(
                                skills = user?.skills ?: emptyList()
                            )
                        }

                        // Contact Information Section
                        PremiumInfoSection(
                            title = "Informations de contact",
                            items = listOfNotNull(
                                PremiumInfoItem(
                                    icon = Icons.Filled.Email,
                                    label = "Email",
                                    value = user?.email ?: "-",
                                    iconColor = MaterialTheme.colorScheme.primary
                                ),
                                PremiumInfoItem(
                                    icon = Icons.Filled.Phone,
                                    label = "Téléphone",
                                    value = user?.phone ?: "-",
                                    iconColor = MaterialTheme.colorScheme.secondary
                                ),
                                if (!user?.location.isNullOrBlank()) {
                                    PremiumInfoItem(
                                        icon = Icons.Filled.LocationOn,
                                        label = "Localisation",
                                        value = user?.location ?: "-",
                                        iconColor = MaterialTheme.colorScheme.tertiary
                                    )
                                } else null,
                                if (!user?.portfolioLink.isNullOrBlank()) {
                                    PremiumInfoItem(
                                        icon = Icons.Filled.Link,
                                        label = "Portfolio",
                                        value = user?.portfolioLink ?: "-",
                                        iconColor = MaterialTheme.colorScheme.primary
                                    )
                                } else null
                            )
                        )
                    }
                }
            }
        }
    }

    // Premium Bottom Sheet Menu
    if (showMenuSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        ModalBottomSheet(
            onDismissRequest = { showMenuSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE0E0E0))
                )
            }
        ) {
            PremiumMenuBottomSheetContent(
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

@Composable
fun PremiumTalentCategorySection(
    talent: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Spécialité",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF6B6B6B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = talent,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun PremiumSkillsSection(
    skills: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Compétences",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(skills) { skill ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = skill,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumInfoSection(
    title: String,
    items: List<PremiumInfoItem>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color(0xFFE8E8E8),
                        thickness = 1.dp
                    )
                }
                
                PremiumInfoRow(item = item)
            }
        }
    }
}

@Composable
fun PremiumInfoRow(
    item: PremiumInfoItem
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Premium Icon Container
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(16.dp),
            color = item.iconColor.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = item.iconColor
                )
            }
        }
        
        // Label and Value
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF6B6B6B),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun PremiumAboutSection(
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "À propos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4A4A4A),
                lineHeight = 26.sp
            )
        }
    }
}

@Composable
fun PremiumMenuBottomSheetContent(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp, top = 8.dp)
    ) {
        PremiumMenuItem(
            icon = Icons.Rounded.Edit,
            title = "Modifier le profil",
            subtitle = "Mettre à jour vos informations",
            onClick = onEditProfile,
            iconColor = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        PremiumMenuItem(
            icon = Icons.Rounded.Settings,
            title = "Paramètres",
            subtitle = "Gérer vos préférences",
            onClick = onSettings,
            iconColor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun PremiumMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = iconColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = iconColor
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B6B6B)
                )
            }
            
            Icon(
                imageVector = Icons.Filled.NavigateNext,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

data class PremiumInfoItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val value: String,
    val iconColor: Color
)

