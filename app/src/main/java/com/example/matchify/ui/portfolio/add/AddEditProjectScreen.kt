package com.example.matchify.ui.portfolio.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    
    // File pickers...
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.addMedia(AttachedMediaItem.ImageMedia(it, context.contentResolver.openInputStream(it)?.readBytes())) } }
    
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.addMedia(AttachedMediaItem.VideoMedia(it)) } }
    
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.addMedia(AttachedMediaItem.PdfMedia(it)) } }
    
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onProjectSaved()
            onBack()
        }
    }

    // Design Colors
    val backgroundColor = Color(0xFF0F172A)
    val cardColor = Color(0xFF1E293B)
    val inputBackgroundColor = Color(0xFF0F172A) // Or slightly different
    val primaryColor = Color(0xFF3B82F6) // Blue
    val textColor = Color.White
    val hintColor = Color(0xFF94A3B8)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (project != null) "Edit Project" else "New Project",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
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
                    Text(
                        text = "Attachments",
                        color = hintColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = cardColor
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (attachedMedia.isNotEmpty()) {
                                attachedMedia.forEachIndexed { index, media ->
                                    if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                                    AttachedMediaRow(
                                        media = media,
                                        onRemove = { viewModel.removeMedia(media) },
                                        textColor = textColor,
                                        subTextColor = hintColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            
                            Button(
                                onClick = { showAttachmentOptions = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF334155), // Lighter than card
                                    contentColor = textColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Attachment")
                            }
                        }
                    }
                }
                
                // Project Details Section
                item {
                    Text(
                        text = "Project Details",
                        color = hintColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = cardColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Title
                            CustomTextField(
                                value = title,
                                onValueChange = { viewModel.setTitle(it) },
                                label = "Title",
                                placeholder = "Project Title",
                                backgroundColor = backgroundColor,
                                textColor = textColor,
                                hintColor = hintColor
                            )
                            
                            // Role
                            CustomTextField(
                                value = role,
                                onValueChange = { viewModel.setRole(it) },
                                label = "Role",
                                placeholder = "Your Role (e.g. Lead Developer)",
                                backgroundColor = backgroundColor,
                                textColor = textColor,
                                hintColor = hintColor
                            )
                            
                            // Description
                            CustomTextField(
                                value = description,
                                onValueChange = { viewModel.setDescription(it) },
                                label = "Description",
                                placeholder = "Project Description...",
                                singleLine = false,
                                minLines = 4,
                                backgroundColor = backgroundColor,
                                textColor = textColor,
                                hintColor = hintColor
                            )
                            
                            // Project Link
                            CustomTextField(
                                value = projectLink,
                                onValueChange = { viewModel.setProjectLink(it) },
                                label = "Project Link",
                                placeholder = "http://example.com",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                backgroundColor = backgroundColor,
                                textColor = textColor,
                                hintColor = hintColor
                            )
                        }
                    }
                }
                
                // Skills Section
                item {
                    Text(
                        text = "Skills",
                        color = hintColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = cardColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (isLoadingSkills) {
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = primaryColor)
                                }
                            } else {
                                val mutableSkills = remember(selectedSkills) { selectedSkills.toMutableList() }
                                SkillPickerView(
                                    selectedSkills = mutableSkills,
                                    onSkillsChanged = { viewModel.updateSelectedSkills(it) },
                                    // You might need to adjust SkillPickerView colors if it doesn't support dark theme via arguments
                                    // Assuming it uses MaterialTheme, we might need a Theme wrapper or updated component.
                                    // For now, let's assume it adapts or we might need to modify it later.
                                )
                            }
                        }
                    }
                }
                
                // Error Message
                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFEF4444),
                            modifier = Modifier.padding(start = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Save Button
            Button(
                onClick = { viewModel.saveProject() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp), // Pill shape from screenshot
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF334155), // Dark grey/blue for button background as per screenshot
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF1E293B),
                    disabledContentColor = Color(0xFF64748B)
                ),
                enabled = title.trim().isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (project != null) "Update Project" else "Create Project",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        // ... Dialogs (reuse existing logic but style if needed)
        if (showAttachmentOptions) {
            // Simple dialog implementation
            AlertDialog(
                onDismissRequest = { showAttachmentOptions = false },
                containerColor = cardColor,
                titleContentColor = textColor,
                textContentColor = hintColor,
                title = { Text("Add Attachment") },
                text = {
                    Column {
                        TextButton(onClick = { showAttachmentOptions = false; imagePickerLauncher.launch("image/*") }) {
                            Text("Image", color = primaryColor)
                        }
                        TextButton(onClick = { showAttachmentOptions = false; videoPickerLauncher.launch("video/*") }) {
                            Text("Video", color = primaryColor)
                        }
                        TextButton(onClick = { showAttachmentOptions = false; pdfPickerLauncher.launch("application/pdf") }) {
                            Text("PDF", color = primaryColor)
                        }
                        TextButton(onClick = { showAttachmentOptions = false; showExternalLinkDialog = true }) {
                            Text("External Link", color = primaryColor)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAttachmentOptions = false }) {
                        Text("Cancel", color = hintColor)
                    }
                }
            )
        }
        
        if (showExternalLinkDialog) {
             AlertDialog(
                onDismissRequest = { showExternalLinkDialog = false },
                containerColor = cardColor,
                titleContentColor = textColor,
                textContentColor = hintColor,
                title = { Text("Add External Link") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextField(
                            value = externalLinkInput,
                            onValueChange = { viewModel.setExternalLinkInput(it) },
                            label = "URL",
                            placeholder = "https://...",
                            backgroundColor = backgroundColor,
                            textColor = textColor,
                            hintColor = hintColor
                        )
                        CustomTextField(
                            value = externalLinkTitle,
                            onValueChange = { viewModel.setExternalLinkTitle(it) },
                            label = "Title",
                            placeholder = "Link Title",
                            backgroundColor = backgroundColor,
                            textColor = textColor,
                            hintColor = hintColor
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.addExternalLink(); showExternalLinkDialog = false },
                        enabled = externalLinkInput.isNotBlank()
                    ) {
                        Text("Add", color = primaryColor)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExternalLinkDialog = false }) {
                        Text("Cancel", color = hintColor)
                    }
                }
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    backgroundColor: Color,
    textColor: Color,
    hintColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Since the screenshot shows label inside? No, looks like label is implied or inside.
        // Usually "Title" is inside the box or above. 
        // Screenshot shows "Title" inside the box (placeholder) and maybe label above?
        // Actually screenshot shows "Project Details" header, then a box with "Title" text inside (placeholder). 
        // When typing, standard behavior.
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = hintColor) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF334155),
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                cursorColor = Color(0xFF3B82F6),
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
private fun AttachedMediaRow(
    media: AttachedMediaItem,
    onRemove: () -> Unit,
    textColor: Color,
    subTextColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF334155), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(media) {
                    is AttachedMediaItem.ImageMedia -> Icons.Default.Image
                    is AttachedMediaItem.VideoMedia -> Icons.Default.VideoLibrary
                    is AttachedMediaItem.PdfMedia -> Icons.Default.PictureAsPdf
                    is AttachedMediaItem.ExternalLinkMedia -> Icons.Default.Link
                    is AttachedMediaItem.ExistingMedia -> Icons.Default.AttachFile
                },
                contentDescription = null,
                tint = textColor
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = media.displayTitle,
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
        
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color(0xFFEF4444)
            )
        }
    }
}
