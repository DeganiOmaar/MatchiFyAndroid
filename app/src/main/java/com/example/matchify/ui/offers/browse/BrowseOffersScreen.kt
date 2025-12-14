package com.example.matchify.ui.offers.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchify.domain.model.OfferCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseOffersScreen(
    onBack: () -> Unit = {},
    onOfferClick: (String) -> Unit = {},
    viewModel: BrowseOffersViewModel = viewModel(factory = BrowseOffersViewModelFactory())
) {
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
                    com.example.matchify.ui.components.CustomAppBar(
                        title = "Browse Offers",
                        profileImageUrl = user?.profileImageUrl,
                        onProfileClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                    
                    // Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                                            text = "Search offers...",
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
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Category Filter Pills
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.height(33.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = if (viewModel.selectedCategory == null) Color(0xFF2563EB) else Color(0xFF111827),
                            onClick = { viewModel.onCategorySelected(null) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight(500),
                                    color = if (viewModel.selectedCategory == null) Color.White else Color(0xFFE5E7EB),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        OfferCategory.values().forEach { category ->
                            Surface(
                                modifier = Modifier.height(33.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = if (viewModel.selectedCategory == category) Color(0xFF2563EB) else Color(0xFF111827),
                                onClick = { viewModel.onCategorySelected(category) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category.displayName,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight(500),
                                        color = if (viewModel.selectedCategory == category) Color.White else Color(0xFFE5E7EB),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
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
                                Icons.Default.SearchOff,
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
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F172A))
                            .padding(paddingValues),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.filteredOffers) { offer ->
                            OfferGridItem(
                                offer = offer,
                                onClick = { onOfferClick(offer.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfferGridItem(
    offer: com.example.matchify.domain.model.Offer,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF111827),
        tonalElevation = 0.dp
    ) {
        Column {
            // Banner Image
            AsyncImage(
                model = "http://10.0.2.2:3000/${offer.bannerImage}",
                contentDescription = "Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Offer Info
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = offer.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(650),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = offer.formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF3B82F6)
                )
                Text(
                    text = offer.category,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

class BrowseOffersViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
        val apiService = com.example.matchify.data.remote.ApiService.getInstance()
        val repository = com.example.matchify.data.remote.OfferRepository(
            apiService.offerApi,
            authPreferences
        )
        @Suppress("UNCHECKED_CAST")
        return BrowseOffersViewModel(repository) as T
    }
}
