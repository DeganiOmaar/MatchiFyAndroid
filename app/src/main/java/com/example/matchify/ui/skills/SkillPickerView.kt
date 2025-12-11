package com.example.matchify.ui.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matchify.domain.model.Skill

@Composable
fun SkillPickerView(
    selectedSkills: MutableList<Skill>,
    onSkillsChanged: (List<Skill>) -> Unit = {}
) {
    val viewModel: SkillPickerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = SkillPickerViewModelFactory()
    )
    var searchText by remember { mutableStateOf("") }
    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val maxSkills = 10
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search field with add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue ->
                    searchText = newValue
                    if (newValue.isNotBlank()) {
                        viewModel.searchSkills(newValue)
                    } else {
                        viewModel.clearSuggestions()
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search skills...") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            
            // Add custom skill button
            if (searchText.isNotBlank() &&
                !suggestions.any { it.name.equals(searchText.trim(), ignoreCase = true) } &&
                !selectedSkills.any { it.name.equals(searchText.trim(), ignoreCase = true) } &&
                selectedSkills.size < maxSkills
            ) {
                IconButton(
                    onClick = {
                        val trimmed = searchText.trim()
                        if (trimmed.isNotBlank()) {
                            val customSkill = Skill(
                                id = null,
                                _id = null,
                                name = trimmed,
                                source = "USER"
                            )
                            val updatedList = selectedSkills.toMutableList()
                            if (!updatedList.any { it.name.equals(trimmed, ignoreCase = true) }) {
                                updatedList.add(customSkill)
                                selectedSkills.clear()
                                selectedSkills.addAll(updatedList)
                                onSkillsChanged(selectedSkills)
                                searchText = ""
                                viewModel.clearSuggestions()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom skill",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Suggestions dropdown
        if (suggestions.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val filteredSuggestions = suggestions.filter { suggestion ->
                        !selectedSkills.any { it.name.equals(suggestion.name, ignoreCase = true) }
                    }
                    
                    items(filteredSuggestions) { skill ->
                        SkillSuggestionItem(
                            skill = skill,
                            onClick = {
                                if (selectedSkills.size < maxSkills &&
                                    !selectedSkills.any { it.name.equals(skill.name, ignoreCase = true) }
                                ) {
                                    val updatedList = selectedSkills.toMutableList()
                                    updatedList.add(skill)
                                    selectedSkills.clear()
                                    selectedSkills.addAll(updatedList)
                                    onSkillsChanged(selectedSkills)
                                    searchText = ""
                                    viewModel.clearSuggestions()
                                }
                            },
                            enabled = selectedSkills.size < maxSkills
                        )
                    }
                }
            }
        }
        
        // Selected skills chips
        if (selectedSkills.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                items(selectedSkills.size) { index ->
                    val skill = selectedSkills[index]
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = skill.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            
                            IconButton(
                                onClick = {
                                    val updatedList = selectedSkills.toMutableList()
                                    updatedList.removeAt(index)
                                    selectedSkills.clear()
                                    selectedSkills.addAll(updatedList)
                                    onSkillsChanged(selectedSkills)
                                },
                                modifier = Modifier.size(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Max skills indicator
        if (selectedSkills.size >= maxSkills) {
            Text(
                text = "Maximum $maxSkills skills allowed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SkillSuggestionItem(
    skill: Skill,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = if (enabled) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = if (skill.isEsco) "ESCO" else "User Created",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (skill.isEsco) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add skill",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

