package com.example.matchify.ui.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchify.data.remote.dto.wallet.PaymentTransactionDto
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transaction: PaymentTransactionDto,
    onBack: () -> Unit
) {
    val darkBackground = Color(0xFF0F172A)
    val cardBackground = Color(0xFF1E293B)
    val textPrimary = Color.White
    val textSecondary = Color(0xFF94A3B8)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details", color = textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        containerColor = darkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Icon
            val statusColor = getStatusColor(transaction.status)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getStatusIcon(transaction.status),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount
            Text(
                text = (if (transaction.direction == "in") "+" else "-") + String.format("€%.2f", transaction.talentAmount),
                color = if (transaction.direction == "in") Color(0xFF10B981) else textPrimary,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = statusColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = transaction.status.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Mission ID", transaction.missionId, textSecondary, textPrimary)
                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Transaction ID", transaction.id, textSecondary, textPrimary)
                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Platform Fee", String.format("€%.2f", transaction.platformFee), textSecondary, textPrimary)
                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow("Date", formatDate(transaction.createdAt), textSecondary, textPrimary)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(title: String, value: String, labelColor: Color, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = labelColor, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private fun getStatusColor(status: String): Color {
    return when (status) {
        "completed" -> Color(0xFF10B981)
        "pending", "processing" -> Color(0xFFF59E0B)
        "failed", "refunded" -> Color(0xFFEF4444)
        else -> Color(0xFF94A3B8)
    }
}

private fun getStatusIcon(status: String) = when (status) {
    "completed" -> Icons.Default.CheckCircle
    "pending", "processing" -> Icons.Default.Schedule
    "failed" -> Icons.Default.Error
    "refunded" -> Icons.Default.KeyboardReturn
    else -> Icons.Default.Schedule
}
