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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
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

import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.UserRepository
import com.example.matchify.data.remote.ApiService

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TalentProfileByIDScreen(
    talentId: String,
    onBack: () -> Unit,
    onRateClick: ((String, String?) -> Unit)? = null,
    viewModel: TalentProfileByIDViewModel = viewModel(
        factory = TalentProfileByIDViewModelFactory(talentId)
    )
) {
    val user by viewModel.user.collectAsState()
    val portfolio by viewModel.portfolio.collectAsState()
    val skillNames by viewModel.skillNames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isLoadingFavorite by viewModel.isLoadingFavorite.collectAsState()
    
    val context = LocalContext.current
    
    // Vérifier si l'utilisateur est un recruteur
    val prefs = remember { AuthPreferencesProvider.getInstance().get() }
    val userRole by prefs.role.collectAsState(initial = "recruiter")
    val isRecruiter = userRole == "recruiter"
    
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        if (isRecruiter) {
            viewModel.checkIfFavorite()
        }
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
                    backgroundColor = darkBackground,
                    isRecruiter = isRecruiter,
                    isFavorite = isFavorite,
                    isLoadingFavorite = isLoadingFavorite,
                    onFavoriteClick = { viewModel.toggleFavorite() }
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
                    talentName = user?.fullName,
                    onRateClick = onRateClick
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
    talentName: String?,
    onRateClick: ((String, String?) -> Unit)? = null
) {
    val ratingViewModel: RatingViewModel = viewModel(factory = RatingViewModelFactory())
    val talentRatingsState by ratingViewModel.talentRatings.collectAsState()
    val myRating by ratingViewModel.myRating.collectAsState()
    val isLoading by ratingViewModel.isLoading.collectAsState()
    
    // Obtenir l'ID du recruteur actuel
    val currentUser by AuthPreferencesProvider.getInstance().get().currentUser.collectAsState()
    val currentRecruiterId = currentUser?.id
    
    // Vérifier si l'utilisateur est un recruteur
    val isRecruiter = remember {
        val prefs = AuthPreferencesProvider.getInstance().get()
        prefs.currentRole.value == "recruiter"
    }
    
    // Recharger les ratings et myRating quand le composable devient visible
    LaunchedEffect(talentId) {
        if (isRecruiter) {
            ratingViewModel.loadTalentRatings(talentId)
            ratingViewModel.loadMyRating(talentId, null)
        }
    }
    
    // Recharger aussi quand on revient sur l'écran (via DisposableEffect)
    DisposableEffect(Unit) {
        if (isRecruiter) {
            ratingViewModel.loadTalentRatings(talentId)
            ratingViewModel.loadMyRating(talentId, null)
        }
        onDispose { }
    }
    
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
    // État pour stocker les noms des recruteurs
    val recruiterNames = remember { mutableStateMapOf<String, String>() }
    val context = LocalContext.current
    val userRepository = remember {
        val prefs = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        UserRepository(apiService.userApi, prefs)
    }
    
    // Scope de coroutine pour charger les noms des recruteurs
    val coroutineScope = rememberCoroutineScope()
    
    // Charger les noms des recruteurs quand les ratings changent
    LaunchedEffect(talentRatingsState) {
        talentRatingsState?.ratings?.forEach { rating ->
            if (!recruiterNames.containsKey(rating.recruiterId)) {
                coroutineScope.launch {
                    try {
                        val (user, _) = userRepository.getUserById(rating.recruiterId)
                        recruiterNames[rating.recruiterId] = user.fullName
                    } catch (e: Exception) {
                        android.util.Log.e("TalentRatingsSection", "Error loading recruiter name: ${e.message}", e)
                        recruiterNames[rating.recruiterId] = "Recruiter"
                    }
                }
            }
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ratings & Feedback",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            
            // Bouton pour donner un rating/feedback
            if (onRateClick != null) {
                Button(
                    onClick = { onRateClick(talentId, talentName) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
        
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
                val talentRatings = talentRatingsState!!
                
                // Score global avec étoiles
                val displayScore = (talentRatingsState as com.example.matchify.ui.ratings.TalentRatingsState).bayesianScore 
                    ?: talentRatings.averageScore
                
                if (displayScore != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackground
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Titre
                            Text(
                                text = "Reviews and ratings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            
                            // Score global avec étoiles
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = String.format("%.1f", displayScore),
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = "/ 5",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textSecondary
                                )
                            }
                            
                            // Étoiles visuelles (arrondi au plus proche)
                            com.example.matchify.ui.ratings.components.RatingStars(
                                rating = kotlin.math.round(displayScore).toInt().coerceIn(1, 5),
                                onRatingChange = {},
                                enabled = false,
                                starSize = 28.dp,
                                starSpacing = 4.dp
                            )
                            
                            // Nombre de ratings
                            Text(
                                text = "Based on ${talentRatings.count} ${if (talentRatings.count == 1) "rating" else "ratings"}",
                                fontSize = 14.sp,
                                color = textSecondary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Liste des reviews individuelles
                if (talentRatings.ratings.isNotEmpty()) {
                    Text(
                        text = "Reviews ${talentRatings.ratings.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                    )
                    
                    talentRatings.ratings.forEach { rating ->
                        // Vérifier si ce rating appartient au recruteur actuel
                        val isMyRating = rating.recruiterId == currentRecruiterId || 
                                        myRating?.id == rating.id
                        
                        // Card avec design similaire à l'image
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Header avec nom, ville et date
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Nom du recruteur (ou "Recruiter" si non disponible)
                                        Text(
                                            text = recruiterNames[rating.recruiterId] ?: "Recruiter",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )
                                        
                                        // Date formatée (ex: "Jan 11")
                                        rating.createdAt?.let { date ->
                                            Text(
                                                text = formatDateForRatingShort(date),
                                                fontSize = 13.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                    
                                    // Bouton de suppression si c'est mon rating
                                    if (isMyRating) {
                                        IconButton(
                                            onClick = { showDeleteDialog = rating.id },
                                            modifier = Modifier.size(32.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color(0xFFEF4444).copy(alpha = 0.1f),
                                                contentColor = Color(0xFFEF4444)
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Supprimer",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                
                                // Étoiles
                                com.example.matchify.ui.ratings.components.RatingStars(
                                    rating = rating.score,
                                    onRatingChange = {},
                                    enabled = false,
                                    starSize = 18.dp,
                                    starSpacing = 2.dp
                                )
                                
                                // Commentaire (titre si disponible, sinon commentaire)
                                if (!rating.comment.isNullOrEmpty()) {
                                    Text(
                                        text = rating.comment ?: "",
                                        fontSize = 14.sp,
                                        color = textPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
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
        
        // Dialog de confirmation de suppression
        showDeleteDialog?.let { ratingId ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = {
                    Text(
                        text = "Supprimer le rating",
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                },
                text = {
                    Text(
                        text = "Êtes-vous sûr de vouloir supprimer ce rating et feedback ?",
                        color = textSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            ratingViewModel.deleteRating(ratingId, talentId)
                            showDeleteDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        )
                    ) {
                        Text("Supprimer", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = null }
                    ) {
                        Text("Annuler", color = textSecondary)
                    }
                },
                containerColor = cardBackground
            )
        }
    }
}

/**
 * Header avec back arrow, titre centré et bouton favori pour les recruteurs
 */
@Composable
private fun ProfileHeader(
    onBack: () -> Unit,
    backgroundColor: Color,
    isRecruiter: Boolean = false,
    isFavorite: Boolean = false,
    isLoadingFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {}
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

            // Favorite button (étoile) for recruiters
            if (isRecruiter) {
                Surface(
                    modifier = Modifier
                        .size(48.dp),
                    color = Color.Transparent,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                enabled = !isLoadingFavorite,
                                onClick = {
                                    android.util.Log.d("ProfileHeader", "Favorite button clicked, isFavorite: $isFavorite")
                                    onFavoriteClick()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoadingFavorite) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = if (isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                                tint = if (isFavorite) Color(0xFFFFD700) else Color(0xFF94A3B8),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            } else {
                // Empty box to balance the layout for non-recruiters
                Spacer(modifier = Modifier.size(48.dp))
            }
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

/**
 * Formater la date pour l'affichage court (ex: "Jan 11")
 */
private fun formatDateForRatingShort(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val outputFormat = SimpleDateFormat("MMM dd", Locale.US)
        val date = inputFormat.parse(dateString) ?: return dateString
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            val inputFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("MMM dd", Locale.US)
            val date = inputFormat2.parse(dateString) ?: return dateString
            outputFormat.format(date)
        } catch (e2: Exception) {
            dateString
        }
    }
}

/**
 * Formater la date pour l'affichage dans les ratings
 */
private fun formatDateForRating(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.FRENCH)
        val date = inputFormat.parse(dateString) ?: return dateString
        outputFormat.format(date)
    } catch (e: Exception) {
        try {
            // Format alternatif
            dateString.substring(0, 10).replace("-", "/")
        } catch (e2: Exception) {
            dateString
        }
    }
}
