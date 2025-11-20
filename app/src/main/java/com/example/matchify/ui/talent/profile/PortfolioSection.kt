package com.example.matchify.ui.talent.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.matchify.domain.model.Project

/**
 * Portfolio Section View for Talent Profile
 * Displays projects in a 2x2 grid with pagination (similar to iOS PortfolioSectionView)
 */
@Composable
fun PortfolioSection(
    projects: List<Project>,
    isLoading: Boolean = false,
    onProjectTap: (Project) -> Unit = {},
    onAddProject: () -> Unit = {},
    showAddButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    val itemsPerPage = 4
    var currentPage by remember { mutableIntStateOf(1) }
    
    val totalPages = kotlin.math.max(1, kotlin.math.ceil(projects.size.toDouble() / itemsPerPage.toDouble()).toInt())
    
    val currentPageProjects: List<Project> = remember(currentPage, projects) {
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = kotlin.math.min(startIndex + itemsPerPage, projects.size)
        if (startIndex >= projects.size) emptyList() else projects.subList(startIndex, endIndex)
    }
    
    val canGoBack = currentPage > 1
    val canGoNext = currentPage < totalPages
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Title and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Projects",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                
                if (showAddButton) {
                    IconButton(
                        onClick = onAddProject,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Project",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Projects Grid or Empty State
            if (isLoading && projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (projects.isEmpty()) {
                EmptyPortfolioState(
                    showAddButton = showAddButton,
                    onAddProject = onAddProject
                )
            } else {
                // 2x2 Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(400.dp)
                ) {
                    items(currentPageProjects) { project ->
                        ProjectGridItem(
                            project = project,
                            onClick = { onProjectTap(project) }
                        )
                    }
                }
                
                // Pagination Controls
                if (totalPages > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { if (canGoBack) currentPage-- },
                            enabled = canGoBack
                        ) {
                            Text(
                                text = "Back",
                                color = if (canGoBack) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color(0xFF6B6B6B).copy(alpha = 0.5f)
                                }
                            )
                        }
                        
                        Text(
                            text = "$currentPage/$totalPages",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1A1A1A)
                        )
                        
                        TextButton(
                            onClick = { if (canGoNext) currentPage++ },
                            enabled = canGoNext
                        ) {
                            Text(
                                text = "Next",
                                color = if (canGoNext) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color(0xFF6B6B6B).copy(alpha = 0.5f)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectGridItem(
    project: Project,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Project Image
        val imageUrl = project.getFirstMediaUrl("http://10.0.2.2:3000")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
        
        // Project Title (Blue)
        Text(
            text = project.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2
        )
    }
}

@Composable
private fun EmptyPortfolioState(
    showAddButton: Boolean,
    onAddProject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(60.dp),
            tint = Color(0xFF6B6B6B).copy(alpha = 0.5f)
        )
        
        Text(
            text = "No Projects Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
        
        Text(
            text = "Add your first project to showcase your work",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF6B6B6B),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        if (showAddButton) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onAddProject,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Add Project")
            }
        }
    }
}

