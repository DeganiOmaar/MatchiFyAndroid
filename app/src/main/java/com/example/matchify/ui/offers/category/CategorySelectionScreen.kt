package com.example.matchify.ui.offers.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.domain.model.OfferCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
    onBack: () -> Unit = {},
    onCategorySelected: (String) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<OfferCategory?>(null) }
    
    // Drawer state for consistency
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
                com.example.matchify.ui.components.MatchifyTopAppBar(
                    title = "Select Category",
                    onBack = onBack
                )
            },
            containerColor = Color(0xFF0F172A)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Choose a category for your service",
                        fontSize = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Category Grid - 2 columns
                    OfferCategory.values().toList().chunked(2).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowCategories.forEach { category ->
                                CategoryCard(
                                    category = category,
                                    isSelected = selectedCategory == category,
                                    onTap = { selectedCategory = category },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty space if odd number
                            if (rowCategories.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Continue Button
                if (selectedCategory != null) {
                    Button(
                        onClick = { onCategorySelected(selectedCategory!!.displayName) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6)
                        )
                    ) {
                        Text(
                            "Continue",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: OfferCategory,
    isSelected: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (category) {
        OfferCategory.DEVELOPMENT -> Icons.Default.Code
        OfferCategory.MARKETING -> Icons.Default.Campaign
        OfferCategory.TEACHING_ONLINE -> Icons.Default.School
        OfferCategory.VIDEO_EDITING -> Icons.Default.Movie
        OfferCategory.COACHING -> Icons.Default.Groups
    }
    
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onTap),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.displayName,
                modifier = Modifier.size(36.dp),
                tint = if (isSelected) Color.White else Color(0xFF3B82F6)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = category.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFFE5E7EB),
                textAlign = TextAlign.Center
            )
        }
    }
}
