package com.example.matchify.ui.talent.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.domain.model.Project

/**
 * Talent Profile Screen avec Material Design 3
 * Implémente toutes les spécifications M3 pour le profil talent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalentProfileScreen(
    viewModel: TalentProfileViewModel,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
    onProjectClick: (Project) -> Unit = {},
    onAddProject: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val user by viewModel.user.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val isLoadingProjects by viewModel.isLoadingProjects.collectAsState()
    
    var showMenuSheet by remember { mutableStateOf(false) }
    var isBioExpanded by remember { mutableStateOf(false) }
    
    // Refresh projects when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            // M3 Top App Bar (Center-Aligned)
            M3TopAppBar(
                onBack = onBack,
                onMoreClick = { showMenuSheet = true }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Error message
            if (errorMessage != null) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Top spacing for App Bar (8dp)
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Profile Avatar (centré, grandes dimensions)
            item {
                ProfileAvatar(
                    imageUrl = user?.profileImageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 12.dp) // M3 spacing: 32-48dp top, 8-16dp bottom
                )
            }

            // User Name (centré)
            item {
                Text(
                    text = user?.fullName ?: "Talent Name",
                    style = MaterialTheme.typography.headlineSmall, // Headline Small ou Title Medium M3
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface, // onBackground / onSurface (adaptatif)
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp) // Spacing avant About Me
                )
            }

            // About Me Section
            if (!user?.description.isNullOrBlank()) {
                item {
                    AboutMeSection(
                        description = user?.description ?: "",
                        isExpanded = isBioExpanded,
                        onExpandedChange = { isBioExpanded = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 32.dp) // Spacing avant Skills
                    )
                }
            }

            // Skills Section
            if (!user?.skills.isNullOrEmpty()) {
                item {
                    SkillsSection(
                        skills = user?.skills ?: emptyList(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 24.dp) // Spacing avant Portfolio
                    )
                }
            }

            // Portfolio Section
            item {
                PortfolioSection(
                    projects = projects,
                    isLoading = isLoadingProjects,
                    onProjectTap = { project ->
                        onProjectClick(project)
                    },
                    onAddProject = {
                        onAddProject()
                    },
                    showAddButton = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
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
            containerColor = MaterialTheme.colorScheme.surface,
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
 * M3 Top App Bar (Center-Aligned)
 * - Titre centré "Profile"
 * - Bouton retour à gauche
 * - Bouton "More" à droite
 * - Élévation 0 (flat)
 */
@Composable
private fun M3TopAppBar(
    onBack: () -> Unit,
    onMoreClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface, // Surface color adaptatif
        shadowElevation = 0.dp, // Élévation 0 (flat)
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 12.dp)
                .padding(top = 8.dp), // Safe area top padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Navigation Icon (gauche)
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Centered Title
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleLarge, // Title Large M3
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // More actions button (droite)
            IconButton(
                onClick = onMoreClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Ligne de séparation subtile
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
            thickness = 1.dp
        )
    }
}

/**
 * Profile Avatar (circulaire, centré, grandes dimensions)
 * M3: 120dp x 120dp, forme circulaire, sans bordure
 */
@Composable
private fun ProfileAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null && imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp) // Large circular avatar
                    .clip(CircleShape), // Material shape system - full circle
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Default Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * About Me Section
 * M3: Title Medium pour le titre, Body Medium pour le texte
 * Support "Read More" avec troncature personnalisée
 */
@Composable
private fun AboutMeSection(
    description: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxChars = 200
    val shouldTruncate = !isExpanded && description.length > maxChars
    val displayText = if (shouldTruncate) {
        description.take(maxChars) + "..."
    } else {
        description
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Title
        Text(
            text = "About Me",
            style = MaterialTheme.typography.titleMedium, // Title Medium M3
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp) // Bottom spacing 8-12dp
        )

        // Body Text avec "Read More"
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium, // Body Medium M3
                color = MaterialTheme.colorScheme.onSurfaceVariant, // onSurfaceVariant tone
                lineHeight = 24.sp, // Paragraph spacing confortable
                textAlign = TextAlign.Start // Align to start (left)
            )

            if (description.length > maxChars) {
                TextButton(
                    onClick = { onExpandedChange(!isExpanded) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isExpanded) "Read Less" else "Read More",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Skills Section
 * M3: Title Medium pour le titre, Assist Chips pour les skills
 */
@Composable
private fun SkillsSection(
    skills: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Text(
            text = "Skills",
            style = MaterialTheme.typography.titleMedium, // Title Medium M3
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp) // Bottom spacing 12dp
        )

        // Skills Chips (M3 Assist Chips) - Horizontal wrapping layout
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 8-12dp between chips
            contentPadding = PaddingValues(0.dp)
        ) {
            items(skills) { skill ->
                SkillChip(skill = skill)
            }
        }
    }
}

/**
 * Skill Chip (M3 Assist Chip)
 * - Pas d'icône (label seulement)
 * - Forme arrondie (M3 default)
 * - Surface container high color
 * - Élévation subtile (1-2dp)
 */
@Composable
private fun SkillChip(skill: String) {
    Surface(
        shape = RoundedCornerShape(20.dp), // Rounded shape M3 default
        color = MaterialTheme.colorScheme.surfaceContainerHigh, // Surface container high
        tonalElevation = 1.dp, // Subtle elevation 1-2dp
        shadowElevation = 1.dp
    ) {
        Text(
            text = skill,
            style = MaterialTheme.typography.bodyMedium, // Body Medium
            color = MaterialTheme.colorScheme.onSurface, // onSurface text
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp) // Horizontal and vertical padding
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
            icon = Icons.Rounded.Edit,
            title = "Edit Profile",
            onClick = onEditProfile,
            iconColor = MaterialTheme.colorScheme.primary
        )

        MenuItem(
            icon = Icons.Rounded.Settings,
            title = "Settings",
            onClick = onSettings,
            iconColor = MaterialTheme.colorScheme.secondary
        )
    }
}

/**
 * Menu Item
 */
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
        color = MaterialTheme.colorScheme.surface
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
