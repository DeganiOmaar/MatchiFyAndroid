package com.example.matchify.ui.offers.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.domain.model.OfferCategory
import com.example.matchify.ui.components.MatchifyTopAppBar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import com.example.matchify.ui.talent.edit.DarkTextField
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class CreateOfferViewModelFactory(
    private val category: OfferCategory
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
        val apiService = com.example.matchify.data.remote.ApiService.getInstance()
        val repository = com.example.matchify.data.remote.OfferRepository(
            apiService.offerApi,
            authPreferences
        )
        @Suppress("UNCHECKED_CAST")
        return CreateOfferViewModel(repository, category) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOfferScreen(
    category: OfferCategory,
    onBack: () -> Unit,
    onOfferCreated: () -> Unit,
    viewModel: CreateOfferViewModel = viewModel(factory = CreateOfferViewModelFactory(category))
) {
    val context = LocalContext.current
    
    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            onOfferCreated()
        }
    }
    
    // Image/Video Pickers
    val bannerImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.bannerImageUri = it }
    }
    
    val galleryImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size <= 10) {
            viewModel.galleryImageUris = uris
        }
    }
    
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.videoUri = it }
    }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val prefs = remember { com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get() }
    val user by prefs.user.collectAsState(initial = null)
    
    com.example.matchify.ui.missions.components.NewDrawerContent(
        drawerState = drawerState,
        currentRoute = null,
        onClose = {
            scope.launch {
                drawerState.close()
            }
        },
        onMenuItemSelected = { itemType ->
            scope.launch {
                drawerState.close()
            }
        }
    ) {
        Scaffold(
            topBar = {
                com.example.matchify.ui.components.CustomAppBar(
                    title = "Create Offer",
                    profileImageUrl = user?.profileImageUrl,
                    onProfileClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            containerColor = Color(0xFF0F172A)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Category Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF3B82F6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = category.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = "Required Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Title
                DarkTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.title = it },
                    placeholder = "Title",
                    leadingIcon = Icons.Default.Title
                )

                // Keywords Section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Keywords",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF9CA3AF)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = viewModel.keywordInput,
                                onValueChange = { viewModel.keywordInput = it },
                                placeholder = { Text("Add keyword", color = Color(0xFF64748B)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF374151),
                                    unfocusedBorderColor = Color(0xFF374151),
                                    focusedContainerColor = Color(0xFF111827),
                                    unfocusedContainerColor = Color(0xFF111827),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            IconButton(
                                onClick = { viewModel.addKeyword() },
                                enabled = viewModel.keywordInput.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "Add",
                                    tint = if (viewModel.keywordInput.isNotEmpty()) Color(0xFF3B82F6) else Color(
                                        0xFF64748B
                                    )
                                )
                            }
                        }

                        if (viewModel.keywords.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(viewModel.keywords) { keyword ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF111827)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = 12.dp,
                                                vertical = 6.dp
                                            ),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(keyword, fontSize = 13.sp, color = Color.White)
                                            Icon(
                                                Icons.Default.Close,
                                                "Remove",
                                                modifier = Modifier.size(14.dp)
                                                    .clickable { viewModel.removeKeyword(keyword) },
                                                tint = Color(0xFF9CA3AF)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Price
                DarkTextField(
                    value = viewModel.price,
                    onValueChange = { viewModel.price = it.filter { char -> char.isDigit() } },
                    placeholder = "Price (€)",
                    leadingIcon = Icons.Default.AttachMoney
                )

                // Description
                DarkTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    placeholder = "Description",
                    leadingIcon = Icons.Default.Description,
                    minLines = 4
                )

                // Banner Image
                Text(
                    text = "Banner Image *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9CA3AF)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable { bannerImagePicker.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (viewModel.bannerImageUri != null) {
                            AsyncImage(
                                model = viewModel.bannerImageUri,
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    "Select",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    "Select Banner Image",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Optional: Gallery Images
                Text(
                    text = "Gallery Images (Optional, max 10)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9CA3AF)
                )

                Button(
                    onClick = { galleryImagesPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B)
                    )
                ) {
                    Icon(Icons.Default.Photo, "Gallery", tint = Color(0xFF3B82F6))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Select Gallery Images (${viewModel.galleryImageUris.size}/10)",
                        color = Color.White
                    )
                }

                // Optional: Video
                Text(
                    text = "Introduction Video (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9CA3AF)
                )

                Button(
                    onClick = { videoPicker.launch("video/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B)
                    )
                ) {
                    Icon(Icons.Default.VideoLibrary, "Video", tint = Color(0xFF3B82F6))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (viewModel.videoUri != null) "Video Selected" else "Select Video",
                        color = Color.White
                    )
                }


                    // Capabilities Section (Optional)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Capabilities (Optional)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF9CA3AF)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = viewModel.capabilityInput,
                                    onValueChange = { viewModel.capabilityInput = it },
                                    placeholder = {
                                        Text(
                                            "Add capability",
                                            color = Color(0xFF64748B)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF374151),
                                        unfocusedBorderColor = Color(0xFF374151),
                                        focusedContainerColor = Color(0xFF111827),
                                        unfocusedContainerColor = Color(0xFF111827),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )

                                IconButton(
                                    onClick = { viewModel.addCapability() },
                                    enabled = viewModel.capabilityInput.isNotEmpty()
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        "Add",
                                        tint = if (viewModel.capabilityInput.isNotEmpty()) Color(
                                            0xFF3B82F6
                                        ) else Color(0xFF64748B)
                                    )
                                }
                            }

                            if (viewModel.capabilities.isNotEmpty()) {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(viewModel.capabilities) { capability ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF3B82F6).copy(alpha = 0.2f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 6.dp
                                                ),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    capability,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF3B82F6)
                                                )
                                                Icon(
                                                    Icons.Default.Close,
                                                    "Remove",
                                                    modifier = Modifier.size(14.dp).clickable {
                                                        viewModel.removeCapability(capability)
                                                    },
                                                    tint = Color(0xFF3B82F6)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Error Message
                    viewModel.error?.let { error ->
                        Text(error, color = Color(0xFFEF4444), fontSize = 14.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Create Button
                    Button(
                        onClick = {
                            // Convert URIs to Files
                            val bannerFile = viewModel.bannerImageUri?.let { uri ->
                                val file = File(
                                    context.cacheDir,
                                    "banner_${System.currentTimeMillis()}.jpg"
                                )
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                file
                            }

                            val galleryFiles = viewModel.galleryImageUris.mapIndexed { index, uri ->
                                val file = File(
                                    context.cacheDir,
                                    "gallery_${index}_${System.currentTimeMillis()}.jpg"
                                )
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                file
                            }

                            val videoFile = viewModel.videoUri?.let { uri ->
                                val file = File(
                                    context.cacheDir,
                                    "video_${System.currentTimeMillis()}.mp4"
                                )
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    FileOutputStream(file).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                file
                            }

                            viewModel.createOffer(bannerFile, galleryFiles, videoFile)
                        },
                        enabled = viewModel.isFormValid() && !viewModel.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            disabledContainerColor = Color(0xFF1E293B)
                        )
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "Create Offer",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
