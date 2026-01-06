package com.example.matchify.ui.talent.matching.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchify.domain.model.Talent


@Composable
fun TalentMatchCard(
    talent: Talent,
    onClick: () -> Unit,
    ratingPercentage: Double?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = talent.fullName ?: "Unknown Talent")
            Text(text = "Rating: ${ratingPercentage ?: "N/A"}%")
        }
    }
}
