package com.example.matchify.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R

/**
 * Reusable AppBar component with profile image, centered title, and optional right action
 * 
 * @param title The centered title text
 * @param profileImageUrl Optional profile image URL
 * @param onProfileClick Callback when profile image is clicked (typically opens drawer)
 * @param rightAction Optional composable for right-side action button
 */
@Composable
fun CustomAppBar(
    title: String,
    profileImageUrl: String? = null,
    onProfileClick: () -> Unit = {},
    rightAction: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp), // 56-60dp
        color = Color(0xFF0F172A)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Profile Image (42dp circular avatar)
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onProfileClick() },
                shape = CircleShape,
                color = Color(0xFF1E293B)
            ) {
                Box {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.avatar),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            
            // Center: Title (perfectly centered, 18-20sp, weight 600-700, white)
            Text(
                text = title,
                fontSize = 19.sp, // 18-20sp
                fontWeight = FontWeight(650), // 600-700
                color = Color(0xFFFFFFFF),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Right: Optional action or spacer for perfect centering
            if (rightAction != null) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    rightAction()
                }
            } else {
                // Empty spacer to balance the layout
                Spacer(modifier = Modifier.size(42.dp))
            }
        }
    }
}
