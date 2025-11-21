package com.example.matchify.ui.recruiter.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
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
    onSettings: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val joined by viewModel.joinedDate.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Fond statique : dégradé bleu vers clair, sans animation
    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF4A6BFF),   // bleu en haut
            Color(0xFF6F7FDB),   // bleu plus doux
            Color(0xFFF5F7FA)    // fond clair en bas
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, 1600f)
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            // Barre simple avec icône paramètres
            Surface(
                modifier = Modifier.fillMaxWidth(),
                // App bar bleu comme le haut de l'écran
                color = Color(0xFF4A6BFF),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Paramètres",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                // Dégradé bleu qui se fond dans un fond clair,
                // pour éviter que tout l'écran soit entièrement bleu.
                .background(brush = backgroundBrush)
        ) {
            // Contenu global
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Avatar qui chevauche le dégradé et la carte
                Box(
                    modifier = Modifier
                        .offset(y = 16.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                ) {
                    val imageUrl = user?.profileImageUrl
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Photo de profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.avatar),
                            placeholder = painterResource(id = R.drawable.avatar)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Carte principale type "About me"
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF2F2F2),
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Nom
                        Text(
                            text = user?.fullName ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )

                        // Sous-titre (rôle + ville)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Recruiter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6F7FDB)
                            )
                            if (!user?.location.isNullOrBlank()) {
                                Text(
                                    text = "  •  ${user?.location}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF9BA2C5)
                                )
                            }
                        }

                        // Bouton Edit profil
                        Button(
                            onClick = onEditProfile,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A6BFF), // bouton bleu
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Edit profile")
                        }

                        // Bloc "About me"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "About me",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF333333)
                            )

                            Text(
                                text = user?.description ?: "No description yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF777777),
                                textAlign = TextAlign.Start,
                                lineHeight = 20.sp
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFE6E6E6)
                        )

                        // Titre infos de contact + contenu
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Contact info",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF333333)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFF5B6BFF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = user?.email ?: "-",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF555555)
                                )
                            }

                            if (!user?.phone.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Phone,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color(0xFF5B6BFF)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = user?.phone ?: "-",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF555555)
                                    )
                                }
                            }

                            // Ligne "Member since" supprimée comme demandé
                        }
                    }
                }

                // Message d'erreur éventuel sous la carte
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
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
        color = Color(0xFFF2F2F2)
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
        color = Color(0xFFF2F2F2)
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
