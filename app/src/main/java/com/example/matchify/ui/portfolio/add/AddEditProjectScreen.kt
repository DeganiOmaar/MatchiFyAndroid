package com.example.matchify.ui.portfolio.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.domain.model.Project
import com.example.matchify.ui.skills.SkillPickerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProjectScreen(
    project: Project? = null,
    onBack: () -> Unit,
    onProjectSaved: () -> Unit = {},
    viewModel: AddEditProjectViewModel = viewModel(
        factory = AddEditProjectViewModelFactory(
            project = project,
            context = LocalContext.current
        )
    )
) {
    val title by viewModel.title.collectAsState()
    val role by viewModel.role.collectAsState()
    val description by viewModel.description.collectAsState()
    val projectLink by viewModel.projectLink.collectAsState()
    val selectedSkills by viewModel.selectedSkills.collectAsState()
    val attachedMedia by viewModel.attachedMedia.collectAsState()
    val externalLinkInput by viewModel.externalLinkInput.collectAsState()
    val externalLinkTitle by viewModel.externalLinkTitle.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val isLoadingSkills by viewModel.isLoadingSkills.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var showExternalLinkDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val imageData = inputStream?.readBytes()
            inputStream?.close()
            viewModel.addMedia(AttachedMediaItem.ImageMedia(it, imageData))
        }
    }
    
    // Video picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addMedia(AttachedMediaItem.VideoMedia(it))
        }
    }
    
    // PDF picker
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addMedia(AttachedMediaItem.PdfMedia(it))
        }
    }
    
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onProjectSaved()
            onBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (project != null) "Edit Project" else "New Project",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Attachments Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Attachments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            if (attachedMedia.isNotEmpty()) {
                                attachedMedia.forEach { media ->
                                    AttachedMediaRow(
                                        media = media,
                                        onRemove = { viewModel.removeMedia(media) }
                                    )
                                }
                            }
                            
                            Button(
                                onClick = { showAttachmentOptions = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Attachment")
                            }
                        }
                    }
                }
                
                // Project Details Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Project Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            OutlinedTextField(
                                value = title,
                                onValueChange = { viewModel.setTitle(it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Title *") },
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = role,
                                onValueChange = { viewModel.setRole(it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Role (e.g., Lead Developer)") },
                                singleLine = true
                            )
                            
                            OutlinedTextField(
                                value = description,
                                onValueChange = { viewModel.setDescription(it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Description") },
                                minLines = 3,
                                maxLines = 10
                            )
                            
                            OutlinedTextField(
                                value = projectLink,
                                onValueChange = { viewModel.setProjectLink(it) },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Project Link (e.g., GitHub URL)") },
                                singleLine = true
                            )
                        }
                    }
                }
                
                // Skills Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Skills",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            if (isLoadingSkills) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                val mutableSkills = remember(selectedSkills) { 
                                    selectedSkills.toMutableList() 
                                }
                                
                                SkillPickerView(
                                    selectedSkills = mutableSkills,
                                    onSkillsChanged = { updated ->
                                        viewModel.updateSelectedSkills(updated)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Error Message
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            
            // Save Button
            Button(
                onClick = { viewModel.saveProject() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = title.trim().isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = if (project != null) "Update Project" else "Create Project",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        // Attachment Options Dialog
        if (showAttachmentOptions) {
            AlertDialog(
                onDismissRequest = { showAttachmentOptions = false },
                title = { Text("Add Attachment") },
                text = {
                    Column {
                        TextButton(onClick = {
                            showAttachmentOptions = false
                            imagePickerLauncher.launch("image/*")
                        }) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Image")
                        }
                        TextButton(onClick = {
                            showAttachmentOptions = false
                            videoPickerLauncher.launch("video/*")
                        }) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Video")
                        }
                        TextButton(onClick = {
                            showAttachmentOptions = false
                            pdfPickerLauncher.launch("application/pdf")
                        }) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PDF")
                        }
                        TextButton(onClick = {
                            showAttachmentOptions = false
                            showExternalLinkDialog = true
                        }) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("External Link")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAttachmentOptions = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        // External Link Dialog
        if (showExternalLinkDialog) {
            AlertDialog(
                onDismissRequest = { showExternalLinkDialog = false },
                title = { Text("Add External Link") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = externalLinkInput,
                            onValueChange = { viewModel.setExternalLinkInput(it) },
                            label = { Text("URL *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = externalLinkTitle,
                            onValueChange = { viewModel.setExternalLinkTitle(it) },
                            label = { Text("Title (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.addExternalLink()
                            showExternalLinkDialog = false
                        },
                        enabled = externalLinkInput.trim().isNotBlank()
                    ) {
                        Text("Add Link")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExternalLinkDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun AttachedMediaRow(
    media: AttachedMediaItem,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preview
            when (media) {
                is AttachedMediaItem.ImageMedia -> {
                    if (media.imageData != null) {
                        // TODO: Display image preview
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    } else {
                        AsyncImage(
                            model = media.uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
                is AttachedMediaItem.VideoMedia -> {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }
                is AttachedMediaItem.PdfMedia -> {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                is AttachedMediaItem.ExternalLinkMedia -> {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                is AttachedMediaItem.ExistingMedia -> {
                    Icon(
                        imageVector = when {
                            media.mediaItem.isImage -> Icons.Default.Image
                            media.mediaItem.isVideo -> Icons.Default.VideoLibrary
                            media.mediaItem.isPdf -> Icons.Default.PictureAsPdf
                            else -> Icons.Default.Link
                        },
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
            
            // Title and info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = media.displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (media is AttachedMediaItem.ExternalLinkMedia) {
                    Text(
                        text = media.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

