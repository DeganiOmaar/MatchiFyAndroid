package com.example.matchify.ui.offers.myoffers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOffersScreen(
    onBack: () -> Unit = {},
    onOfferClick: (String) -> Unit = {},
    viewModel: MyOffersViewModel = viewModel(factory = MyOffersViewModelFactory())
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                ) {
                    com.example.matchify.ui.components.MatchifyTopAppBar(
                        title = "My Offers",
                        onBack = onBack
                    )
                    
                    // Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(19.dp)
                            )
                            
                            BasicTextField(
                                value = viewModel.searchText,
                                onValueChange = { viewModel.onSearchTextChange(it) },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight(400),
                                    color = Color.White
                                ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (viewModel.searchText.isEmpty()) {
                                        Text(
                                            text = "Search my offers...",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight(400),
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    innerTextField()
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFF0F172A)
        ) { paddingValues ->
            when {
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF3B82F6))
                    }
                }
                viewModel.filteredOffers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.WorkOff,
                                "No offers",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White.copy(alpha = 0.5f)
                            )
                            Text(
                                "No offers found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                "You have not created any offers yet.",
                                fontSize = 15.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F172A))
                            .padding(paddingValues),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(viewModel.filteredOffers) { offer ->
                            OfferRowItem(
                                offer = offer,
                                onEdit = { onOfferClick(offer.id) },
                                onDelete = { showDeleteDialog = offer.id }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    showDeleteDialog?.let { offerId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Offer", color = Color.White) },
            text = { Text("Are you sure you want to delete this offer?", color = Color(0xFF9CA3AF)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOffer(viewModel.offers.first { it.id == offerId })
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = Color(0xFF3B82F6))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@Composable
fun OfferRowItem(
    offer: com.example.matchify.domain.model.Offer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF111827),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onEdit)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner Image
            AsyncImage(
                model = "http://10.0.2.2:3000/${offer.bannerImage}",
                contentDescription = "Banner",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Offer Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = offer.title,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight(650),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = offer.formattedPrice,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3B82F6)
                )
                Text(
                    text = offer.category,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF3B82F6))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

class MyOffersViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
        val apiService = com.example.matchify.data.remote.ApiService.getInstance()
        val repository = com.example.matchify.data.remote.OfferRepository(
            apiService.offerApi,
            authPreferences
        )
        @Suppress("UNCHECKED_CAST")
        return MyOffersViewModel(repository) as T
    }
}
