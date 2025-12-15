package com.example.matchify.ui.portfolio.details

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.matchify.domain.model.Project
import com.example.matchify.data.local.AuthPreferencesProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    projectId: String,
    onBack: () -> Unit,
    onEditProject: (Project) -> Unit = {},
    onDeleteProject: () -> Unit = {},
    viewModel: ProjectDetailsViewModel = viewModel(factory = ProjectDetailsViewModelFactory(projectId)),
    navController: NavController? = null
) {
    val project by viewModel.project.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()
    val context = LocalContext.current
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    // Check if current user is the owner of the project
    val currentUser by AuthPreferencesProvider.getInstance().get().currentUser.collectAsState()
    val currentUserId = currentUser?.id
    val isOwner = project?.talentId == currentUserId
    
    // Debug: Log project ID and loading state
    LaunchedEffect(projectId) {
        android.util.Log.d("ProjectDetailsScreen", "Screen initialized with projectId: $projectId")
    }
    
    // Reload project if it's null after loading completes
    LaunchedEffect(isLoading, project) {
        if (!isLoading && project == null && errorMessage == null) {
            android.util.Log.w("ProjectDetailsScreen", "Project is null after loading, attempting reload")
            viewModel.loadProject()
        }
    }
    
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            onDeleteProject()
            onBack()
        }
    }
    
    // Design colors
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val whiteText = Color.White
    val grayText = Color(0xFF94A3B8)
    
    Scaffold(
        containerColor = darkBackground,
        topBar = {
            Surface(
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
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = whiteText
                        )
                    }
                    
                    Text(
                        text = "Project Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = whiteText
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (project != null && isOwner) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete project",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                            TextButton(onClick = { 
                                project?.let { 
                                    onEditProject(it) 
                                } 
                            }) {
                                Text(
                                    text = "Edit",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = whiteText
                                )
                            }
                        } else {
                            // Empty space to balance layout
                            Spacer(modifier = Modifier.width(80.dp))
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
                .background(darkBackground)
        ) {
            when {
                isLoading && project == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = whiteText
                    )
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error",
                        color = Color(0xFFEF4444),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                project == null -> {
                    Text(
                        text = "Project not found",
                        color = grayText,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Media Section
                        if (project!!.media.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Media",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = whiteText
                                )
                                
                                // First media item (large)
                                val firstMedia = project!!.firstMediaItem
                                val mediaUrl = firstMedia?.getMediaUrl("http://10.0.2.2:3000")
                                
                                if (mediaUrl != null) {
                                    AsyncImage(
                                        model = mediaUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(240.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        
                        // Project Title
                        Text(
                            text = project!!.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = whiteText
                        )
                        
                        // Role
                        if (!project!!.role.isNullOrBlank()) {
                            Text(
                                text = project!!.role!!,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = grayText
                            )
                        }
                        
                        // Description
                        if (!project!!.description.isNullOrBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Description",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = grayText
                                )
                                Text(
                                    text = project!!.description!!,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = whiteText,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        
                        // Skills
                        if (project!!.skills.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Skills",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = grayText
                                )
                                
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    items(project!!.skills) { skill ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = cardBackground
                                        ) {
                                            Text(
                                                text = skill,
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                fontSize = 14.sp,
                                                color = whiteText,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Project Link
                        if (!project!!.projectLink.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = cardBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project!!.projectLink))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = project!!.projectLink!!,
                                        fontSize = 14.sp,
                                        color = Color(0xFF3B82F6),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = "Open link",
                                        tint = Color(0xFF3B82F6)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = cardBackground,
                titleContentColor = whiteText,
                textContentColor = grayText,
                title = {
                    Text(
                        text = "Delete Project",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete this project? This action cannot be undone."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProject()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = whiteText
                            )
                        } else {
                            Text("Delete", color = whiteText)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = grayText)
                    }
                }
            )
        }
    }
}
