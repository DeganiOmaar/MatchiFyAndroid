package com.example.matchify.ui.conversations

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchify.R
import com.example.matchify.domain.model.Deliverable
import com.example.matchify.domain.model.Message

@Composable
fun DeliverableMessageBubble(
    message: Message,
    isFromCurrentUser: Boolean,
    isRecruiter: Boolean,
    onApprove: (String) -> Unit,
    onRequestChanges: (String, String) -> Unit, // deliverableId, reason
    currentUserAvatarUrl: String?,
    otherUserAvatarUrl: String?
) {
    val context = LocalContext.current
    val deliverable = message.deliverable ?: return
    
    var showRequestChangesDialog by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Avatar for received messages
        if (!isFromCurrentUser) {
            AsyncImage(
                model = otherUserAvatarUrl ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        Column(
            horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = screenWidth * 0.8f)
        ) {
            val bubbleColor = if (isFromCurrentUser) Color(0xFF3B82F6) else Color(0xFF374151)
            val bubbleShape = if (isFromCurrentUser) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 0.dp)
            } else {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 16.dp)
            }
            
            Surface(
                shape = bubbleShape,
                color = bubbleColor,
                onClick = {
                    // Open link or file
                    val urlToOpen = deliverable.url ?: deliverable.fileUrl
                    urlToOpen?.let {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // File/Link icon and name
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (deliverable.isLink) Icons.Default.Link else Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = deliverable.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            
                            // Status badge
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = getStatusColor(deliverable.status).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = deliverable.statusDisplayName,
                                        fontSize = 12.sp,
                                        color = getStatusColor(deliverable.status),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                
                                if (deliverable.isLink) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.Blue.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Link",
                                            fontSize = 10.sp,
                                            color = Color(0xFF60A5FA),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Rejection reason
                    if (deliverable.status == "revision_requested" && !deliverable.rejectionReason.isNullOrEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Revision requested:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFEF4444)
                                )
                                Text(
                                    text = deliverable.rejectionReason!!,
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444).copy(alpha = 0.9f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    
                    // Recruiter actions
                    if (isRecruiter && deliverable.status == "pending_review" && !isFromCurrentUser) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showRequestChangesDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                            ) {
                                Text("Request Changes", fontSize = 12.sp)
                            }
                            
                            Button(
                                onClick = {
                                    android.util.Log.d("DeliverableMessageBubble", "Approve button clicked directly")
                                    // android.widget.Toast.makeText(context, "Clicked!", android.widget.Toast.LENGTH_SHORT).show()
                                    onApprove(deliverable.deliverableId)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF3B82F6)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve & Pay", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Request Changes Dialog
    if (showRequestChangesDialog) {
        AlertDialog(
            onDismissRequest = { showRequestChangesDialog = false },
            title = { Text("Request Changes") },
            text = {
                OutlinedTextField(
                    value = rejectionReason,
                    onValueChange = { rejectionReason = it },
                    label = { Text("Reason for changes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectionReason.isNotEmpty()) {
                            onRequestChanges(deliverable.deliverableId, rejectionReason)
                            showRequestChangesDialog = false
                            rejectionReason = ""
                        }
                    },
                    enabled = rejectionReason.isNotEmpty()
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestChangesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "approved" -> Color(0xFF10B981)
        "revision_requested" -> Color(0xFFEF4444)
        "pending_review" -> Color(0xFFF59E0B)
        else -> Color(0xFF9CA3AF)
    }
}
