package com.example.matchify.ui.talent.profilebyid

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.domain.model.Project
import com.example.matchify.ui.ratings.RatingViewModel
import com.example.matchify.ui.ratings.RatingViewModelFactory
import com.example.matchify.ui.ratings.components.AverageRatingCard
import com.example.matchify.ui.ratings.components.RatingCard
import com.example.matchify.data.local.AuthPreferencesProvider
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Star

@Composable
fun TalentProfileByIDScreen(
    talentId: String,
    onBack: () -> Unit,
    onRateClick: (String, String?) -> Unit = { _, _ -> },
    viewModel: TalentProfileByIDViewModel = viewModel(
        factory = TalentProfileByIDViewModelFactory(talentId)
    )
) {
    val user by viewModel.user.collectAsState()
    val portfolio by viewModel.portfolio.collectAsState()
    val skillNames by viewModel.skillNames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }
    
    // Couleurs du thème sombre (identiques à TalentProfileScreen)
    val darkBackground = Color(0xFF0F172A)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF3B82F6)
            )
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Error loading profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFEF4444)
                )
                Text(
                    text = errorMessage ?: "Unknown error",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
                Button(onClick = { viewModel.loadProfile() }) {
                    Text("Retry")
                }
            }
        } else if (user != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                ProfileHeader(
                    onBack = onBack,
                    backgroundColor = darkBackground
                )

                // Profile Avatar Section
                ProfileAvatarSection(
                    imageUrl = user?.profileImageUrl,
                    name = user?.fullName ?: "Talent Name",
                    email = user?.email ?: "",
                    talent = user?.talent?.joinToString(", ") ?: ""
                )

                // Bio Section
                if (!user?.description.isNullOrBlank()) {
                    BioSection(
                        bio = user?.description ?: ""
                    )
                }

                // Skills Section
                if (skillNames.isNotEmpty()) {
                    SkillsSection(
                        skills = skillNames
                    )
                }

                // Portfolio Section
                PortfolioSection(
                    projects = portfolio,
                    isLoading = false,
                    onProjectClick = { /* Read only for now or implement detail view */ }
                )

                // CV File Section
                user?.cvUrl?.let { cvUrl ->
                    if (cvUrl.isNotBlank()) {
                        CvFileSection(
                            cvFileName = "${user?.fullName ?: "Talent"} - CV.pdf",
                            onCvClick = {
                                val cvUrlFull = user?.cvUrlURL ?: return@CvFileSection
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cvUrlFull))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    android.util.Log.e("TalentProfileByID", "Error opening CV: ${e.message}", e)
                                }
                            }
                        )
                    }
                }
                
                // Ratings Section (for recruiters viewing talent profile)
                TalentRatingsSection(
                    talentId = talentId,
                    talentName = user?.fullName
                )
                
                // Ratings Section (for recruiters viewing talent profile)
                TalentRatingsSection(
                    talentId = talentId,
                    talentName = user?.fullName
                )
                
                // Bottom spacing
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

/**
 * Section pour afficher les ratings d'un talent
 */
@Composable
private fun TalentRatingsSection(
    talentId: String,
    talentName: String?
) {
    val ratingViewModel: RatingViewModel = viewModel(factory = RatingViewModelFactory())
    val talentRatingsState by ratingViewModel.talentRatings.collectAsState()
    val isLoading by ratingViewModel.isLoading.collectAsState()
    
    // Vérifier si l'utilisateur est un recruteur
    val isRecruiter = remember {
        val prefs = AuthPreferencesProvider.getInstance().get()
        prefs.currentRole.value == "recruiter"
    }
    
    LaunchedEffect(talentId) {
        if (isRecruiter) {
            ratingViewModel.loadTalentRatings(talentId)
        }
    }
    
    if (!isRecruiter) return
    
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF9CA3AF)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ratings & Feedback",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            talentRatingsState != null && talentRatingsState!!.count > 0 -> {
                // Extraire la valeur non-null pour éviter le problème de smart cast
                val talentRatings = talentRatingsState!!
                
                // Average rating card
                AverageRatingCard(
                    averageScore = talentRatings.averageScore ?: 0.0,
                    count = talentRatings.count
                )
                
                // Individual ratings
                if (talentRatings.ratings.isNotEmpty()) {
                    Text(
                        text = "Recent Feedback",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    talentRatings.ratings.take(3).forEach { rating ->
                        RatingCard(
                            rating = rating,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            else -> {
                Text(
                    text = "No ratings yet",
                    fontSize = 14.sp,
                    color = textSecondary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

/**
 * Header avec back arrow et titre centré (sans menu edit)
 */
@Composable
private fun ProfileHeader(
    onBack: () -> Unit,
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

            // Centered title (using weight to center properly)
            Text(
                text = "Talent Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight(600),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Empty box to balance the layout
            Spacer(modifier = Modifier.size(40.dp))
        }
    }
}

/**
 * Profile Avatar Section avec image circulaire, nom, email et talent
 */
@Composable
private fun ProfileAvatarSection(
    imageUrl: String?,
    name: String,
    email: String,
    talent: String
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

        // Talent
        if (talent.isNotBlank()) {
            Text(
                text = "Talent : $talent",
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
 * Skills Section avec chips bleus qui wrap
 */
@Composable
private fun SkillsSection(
    skills: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 8.dp)
    ) {
        // Section title - grand texte blanc gras
        Text(
            text = "Skills",
            fontSize = 24.sp,
            fontWeight = FontWeight(700),
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Skills chips avec wrapping
        FlowRowLayout(
            items = skills,
            horizontalSpacing = 8.dp,
            verticalSpacing = 8.dp
        ) { skill ->
            SkillChip(skill = skill)
        }
    }
}

/**
 * Layout personnalisé qui wrap les items comme FlowRow
 */
@Composable
private fun FlowRowLayout(
    items: List<String>,
    horizontalSpacing: androidx.compose.ui.unit.Dp,
    verticalSpacing: androidx.compose.ui.unit.Dp,
    content: @Composable (String) -> Unit
) {
    BoxWithConstraints {
        val maxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val density = LocalDensity.current
        
        // Calculer les lignes en fonction de la largeur disponible
        val rowsList = mutableListOf<MutableList<String>>()
        var currentRow = mutableListOf<String>()
        var currentRowWidth = 0f
        
        items.forEach { skill ->
            // Estimation de la largeur (approximative en dp)
            val estimatedWidthDp = (skill.length * 7 + 32).dp
            val estimatedWidthPx = with(density) { estimatedWidthDp.toPx() }
            
            if (currentRowWidth + estimatedWidthPx > maxWidthPx && currentRow.isNotEmpty()) {
                // Nouvelle ligne
                rowsList.add(currentRow.toMutableList())
                currentRow = mutableListOf(skill)
                currentRowWidth = estimatedWidthPx
            } else {
                currentRow.add(skill)
                currentRowWidth += estimatedWidthPx + with(density) { horizontalSpacing.toPx() }
            }
        }
        
        // Dernière ligne
        if (currentRow.isNotEmpty()) {
            rowsList.add(currentRow)
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(verticalSpacing)
        ) {
            rowsList.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
                ) {
                    row.forEach { skill ->
                        content(skill)
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillChip(skill: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E3A8A), // Bleu foncé
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Text(
            text = skill,
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF60A5FA), // Bleu clair pour le texte
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

/**
 * Portfolio Section avec grille 2x2
 */
@Composable
private fun PortfolioSection(
    projects: List<Project>,
    isLoading: Boolean,
    onProjectClick: (Project) -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 4
    val totalPages = kotlin.math.max(1, kotlin.math.ceil(projects.size.toDouble() / itemsPerPage.toDouble()).toInt())
    
    val startIndex = currentPage * itemsPerPage
    val endIndex = kotlin.math.min(startIndex + itemsPerPage, projects.size)
    val currentProjects = if (startIndex < projects.size) {
        projects.subList(startIndex, endIndex)
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
    ) {
        // Section title - grand texte en haut à gauche
        Text(
            text = "Portfolio",
            fontSize = 24.sp,
            fontWeight = FontWeight(700),
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        if (isLoading && projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (projects.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No portfolio items",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )
            }
        } else {
            // Grille 2x2
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Première ligne (2 items)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top-left card
                    if (currentProjects.isNotEmpty()) {
                        PortfolioItemCard(
                            project = currentProjects[0],
                            onClick = { onProjectClick(currentProjects[0]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    // Top-right card
                    if (currentProjects.size > 1) {
                        PortfolioItemCard(
                            project = currentProjects[1],
                            onClick = { onProjectClick(currentProjects[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                
                // Deuxième ligne (2 items)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bottom-left card
                    if (currentProjects.size > 2) {
                        PortfolioItemCard(
                            project = currentProjects[2],
                            onClick = { onProjectClick(currentProjects[2]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    
                    // Bottom-right card
                    if (currentProjects.size > 3) {
                        PortfolioItemCard(
                            project = currentProjects[3],
                            onClick = { onProjectClick(currentProjects[3]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Pagination control horizontal - boutons aux extrémités
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton gauche circulaire à gauche
                IconButton(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF1E293B),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Indicateur de pagination centré
                Text(
                    text = "${currentPage + 1} / $totalPages",
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFE5E7EB)
                )
                
                // Bouton droit circulaire à droite
                IconButton(
                    onClick = { if (currentPage < totalPages - 1) currentPage++ },
                    enabled = currentPage < totalPages - 1,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFF1E293B),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioItemCard(
    project: Project,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        // Portfolio image avec gradient placeholder
        val imageUrl = project.getFirstMediaUrl("http://10.0.2.2:3000")
        
        // Générer un gradient unique basé sur l'index du projet
        val gradientColors = remember(project.id) {
            generateGradientColors(project.id ?: project.title)
        }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E293B)
        ) {
            Box {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Gradient placeholder avec couleurs variées
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = gradientColors
                                )
                            )
                    )
                }
            }
        }

        // Portfolio description en dessous
        Text(
            text = project.description ?: project.title,
            fontSize = 13.sp,
            fontWeight = FontWeight(400),
            color = Color(0xFFE5E7EB),
            maxLines = 2,
            modifier = Modifier.padding(top = 12.dp),
            lineHeight = 18.sp
        )
    }
}

/**
 * Génère des couleurs de gradient uniques basées sur un identifiant
 */
private fun generateGradientColors(seed: String): List<Color> {
    val hash = seed.hashCode()
    val colors = listOf(
        // Gradient 1: Purple to Pink
        listOf(Color(0xFF4C1D95), Color(0xFF7C3AED), Color(0xFFEC4899)),
        // Gradient 2: Teal to Blue
        listOf(Color(0xFF0F766E), Color(0xFF14B8A6), Color(0xFF64748B)),
        // Gradient 3: Purple to Cyan
        listOf(Color(0xFF5B21B6), Color(0xFF6366F1), Color(0xFF06B6D4)),
        // Gradient 4: Orange to Cream
        listOf(Color(0xFFEA580C), Color(0xFFF97316), Color(0xFFFEF3C7))
    )
    return colors[kotlin.math.abs(hash) % colors.size]
}

/**
 * CV File Section
 */
@Composable
private fun CvFileSection(
    cvFileName: String,
    onCvClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
    ) {
        // Section title
        Text(
            text = "CV File",
            fontSize = 16.sp,
            fontWeight = FontWeight(600),
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // File container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCvClick),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = cvFileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFFE5E7EB),
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Preview CV",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Helper function to extract filename from URL
 */
private fun extractFileNameFromUrl(url: String): String? {
    return try {
        val uri = Uri.parse(url)
        uri.lastPathSegment ?: "CV.pdf"
    } catch (e: Exception) {
        null
    }
}
