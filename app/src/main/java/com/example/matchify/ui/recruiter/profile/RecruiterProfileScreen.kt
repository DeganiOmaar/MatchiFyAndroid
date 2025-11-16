package com.example.matchify.ui.recruiter.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruiterProfileScreen(
    viewModel: RecruiterProfileViewModel,
    onEditProfile: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val joined by viewModel.joinedDate.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showMoreSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 20.dp, top = 8.dp)
        ) {
            // Error message
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Banner Image
            item {
                Image(
                    painter = painterResource(id = R.drawable.banner),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Avatar with white border and shadow
            item {
                Box(
                    modifier = Modifier
                        .offset(y = (-60).dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user?.profileImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .shadow(4.dp, shape = CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.avatar),
                        placeholder = painterResource(id = R.drawable.avatar)
                    )
                }
            }

            // Spacer to account for negative offset
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Name - 26sp, bold
            item {
                Text(
                    text = user?.fullName ?: "Recruiter Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Email - 16sp, gray
            item {
                Text(
                    text = user?.email ?: "-",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            // Action Buttons Row (Follow, Message, More)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileActionButton(
                        icon = R.drawable.ic_person,
                        text = "Follow",
                        onClick = { /* TODO: Implement follow */ }
                    )
                    ProfileActionButton(
                        icon = R.drawable.ic_bubble,
                        text = "Message",
                        onClick = { /* TODO: Implement message */ }
                    )
                    ProfileActionButton(
                        icon = R.drawable.ic_ellipsis,
                        text = "More",
                        onClick = { showMoreSheet = true }
                    )
                }
            }

            // Description Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = if (user?.description.isNullOrBlank()) {
                            "You can add a description about yourself."
                        } else {
                            user?.description ?: ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
            }

            // Information Card
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Title
                        Text(
                            text = "Information",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Info Rows
                        InfoRow(
                            icon = R.drawable.ic_email,
                            title = "Email",
                            value = user?.email ?: "-"
                        )
                        InfoRow(
                            icon = R.drawable.ic_phone,
                            title = "Phone",
                            value = user?.phone ?: "-"
                        )
                        InfoRow(
                            icon = R.drawable.ic_calendar,
                            title = "Joined",
                            value = joined
                        )
                    }
                }
            }
        }
    }

    // More Bottom Sheet
    if (showMoreSheet) {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false
        )
        ModalBottomSheet(
            onDismissRequest = { showMoreSheet = false },
            sheetState = sheetState,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }
        ) {
            MoreBottomSheetContent(
                onEditProfile = {
                    showMoreSheet = false
                    onEditProfile()
                },
                onSettings = {
                    showMoreSheet = false
                    // TODO: Navigate to settings
                }
            )
        }
    }
}

@Composable
fun ProfileActionButton(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}

@Composable
fun InfoRow(
    icon: Int,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp),
            tint = Color.Gray.copy(alpha = 0.8f)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                color = Color.Gray.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MoreBottomSheetContent(
    onEditProfile: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Title
        Text(
            text = "More",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Divider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        )

        // Edit Profile Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditProfile() }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.Black
            )
            Text(
                text = "Edit Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Divider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        )

        // Settings Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSettings() }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.Black
            )
            Text(
                text = "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
