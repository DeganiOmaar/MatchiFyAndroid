package com.example.matchify.ui.offers.edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.matchify.domain.model.Offer
import com.example.matchify.ui.components.MatchifyTopAppBar
import com.example.matchify.ui.talent.edit.DarkTextField
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOfferScreen(
    offer: Offer,
    onBack: () -> Unit,
    onOfferUpdated: () -> Unit,
    viewModel: EditOfferViewModel = viewModel(factory = EditOfferViewModelFactory(offer))
) {
    val context = LocalContext.current
    
    // Pickers
    val bannerPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setNewBanner(it) }
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.addNewGalleryImages(uris)
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setNewVideo(it) }
    }

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            onOfferUpdated()
        }
    }
    
    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            MatchifyTopAppBar(
                title = "Edit Offer",
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Edit Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Title
            Text("Title", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            DarkTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                placeholder = "aaa",
                leadingIcon = Icons.Rounded.Title,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Keywords
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Keywords",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9CA3AF)
                    )
                    
                    OutlinedTextField(
                        value = viewModel.keywordInput,
                        onValueChange = { viewModel.keywordInput = it },
                        placeholder = { Text("Add keyword", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF374151),
                            unfocusedBorderColor = Color(0xFF374151),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.addKeyword() },
                                enabled = viewModel.keywordInput.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Rounded.Add,
                                    "Add",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                    )
                    
                    if (viewModel.keywords.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(viewModel.keywords) { keyword ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF334155)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(keyword, fontSize = 14.sp, color = Color(0xFF94A3B8))
                                        Icon(
                                            Icons.Rounded.Close,
                                            "Remove",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.removeKeyword(keyword) },
                                            tint = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Price
            Text("Price (€)", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            DarkTextField(
                value = viewModel.price,
                onValueChange = { viewModel.price = it.filter { char -> char.isDigit() } },
                placeholder = "11111",
                leadingIcon = Icons.Rounded.AttachMoney,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Description
            Text("Description", color = Color(0xFF9CA3AF), fontSize = 14.sp)
            DarkTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                placeholder = "dvdv",
                leadingIcon = Icons.Rounded.Description,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                singleLine = false,
                maxLines = 5
            )
            
            // Capabilities
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Capabilities",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF9CA3AF)
                    )
                    
                    OutlinedTextField(
                        value = viewModel.capabilityInput,
                        onValueChange = { viewModel.capabilityInput = it },
                        placeholder = { Text("Add capability", color = Color(0xFF64748B)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF374151),
                            unfocusedBorderColor = Color(0xFF374151),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.addCapability() },
                                enabled = viewModel.capabilityInput.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Rounded.Add,
                                    "Add",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }
                    )
                    
                    if (viewModel.capabilities.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(viewModel.capabilities) { capability ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF334155)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(capability, fontSize = 14.sp, color = Color(0xFF94A3B8))
                                        Icon(
                                            Icons.Rounded.Close,
                                            "Remove",
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clickable { viewModel.removeCapability(capability) },
                                            tint = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Banner Section
            Text(
                "Update Banner (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                onClick = { bannerPicker.launch("image/*") }
            ) {
                Box(modifier = Modifier.height(200.dp)) {
                    val model = viewModel.newBannerUri ?: "http://10.0.2.2:3000/${offer.bannerImage}"
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(model)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = "Edit",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Text("Tap to change", color = Color.White)
                        }
                    }
                }
            }

            // Gallery Section
            Text(
                "Update Gallery (Replaces Existing)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    // Add Button
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clickable { galleryPicker.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, "Add", tint = Color(0xFF3B82F6))
                            Text("Select New", color = Color(0xFF3B82F6), fontSize = 12.sp)
                        }
                    }
                }
                
                // Show new images if selected, otherwise show existing
                if (viewModel.newGalleryUris.isNotEmpty()) {
                    items(viewModel.newGalleryUris) { uri ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.removeNewGalleryImage(uri) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(Icons.Rounded.Cancel, "Remove", tint = Color.Red)
                            }
                        }
                    }
                } else if (!offer.galleryImages.isNullOrEmpty()) {
                    items(offer.galleryImages) { imagePath ->
                        AsyncImage(
                            model = "http://10.0.2.2:3000/$imagePath",
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Video Section
            Text(
                "Update Video (Optional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            Button(
                onClick = { videoPicker.launch("video/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
            ) {
                Icon(Icons.Rounded.VideoLibrary, "Video", tint = Color(0xFF3B82F6))
                Spacer(Modifier.width(8.dp))
                val videoLabel = when {
                    viewModel.newVideoUri != null -> "New Video Selected"
                    !offer.introductionVideo.isNullOrEmpty() -> "Has Existing Video (Tap to Replace)"
                    else -> "Select Video"
                }
                Text(videoLabel, color = Color.White)
            }
            
            // Error
            viewModel.error?.let { error ->
                Text(error, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(10.dp))
            
            // Update Button
            Button(
                onClick = { 
                    try {
                        // Convert Uris to Files
                        val bannerFile = viewModel.newBannerUri?.let { uri ->
                            val file = File(context.cacheDir, "banner_update_${System.currentTimeMillis()}.jpg")
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                FileOutputStream(file).use { output -> input.copyTo(output) }
                            }
                            file
                        }
                        
                        val galleryFiles = viewModel.newGalleryUris.mapIndexed { index, uri ->
                             val file = File(context.cacheDir, "gallery_update_${index}_${System.currentTimeMillis()}.jpg")
                             context.contentResolver.openInputStream(uri)?.use { input ->
                                 FileOutputStream(file).use { output -> input.copyTo(output) }
                             }
                             file
                        }
                        
                        val videoFile = viewModel.newVideoUri?.let { uri ->
                            val file = File(context.cacheDir, "video_update_${System.currentTimeMillis()}.mp4")
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                FileOutputStream(file).use { output -> input.copyTo(output) }
                            }
                            file
                        }
                        
                        viewModel.updateOffer(
                            bannerImageFile = bannerFile,
                            galleryImageFiles = galleryFiles.takeIf { it.isNotEmpty() },
                            videoFile = videoFile,
                            onSuccess = onOfferUpdated
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Text("Update Offer", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(Modifier.height(30.dp))
        }
    }
}

class EditOfferViewModelFactory(
    private val offer: Offer
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
        val apiService = com.example.matchify.data.remote.ApiService.getInstance()
        val repository = com.example.matchify.data.remote.OfferRepository(
            apiService.offerApi,
            authPreferences
        )
        @Suppress("UNCHECKED_CAST")
        return EditOfferViewModel(repository, offer) as T
    }
}
