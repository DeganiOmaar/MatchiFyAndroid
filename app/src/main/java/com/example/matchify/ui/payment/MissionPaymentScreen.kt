package com.example.matchify.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchify.domain.model.Mission
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

@Composable
fun MissionPaymentScreen(
    missionId: String,
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: MissionPaymentViewModel = viewModel(
        factory = MissionPaymentViewModelFactory(missionId)
    )
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val mission by viewModel.mission.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val isRecruiter = viewModel.isRecruiter

    // Handle success alert
    if (successMessage != null) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss on outside click */ },
            title = { Text("Success") },
            text = { Text(successMessage ?: "") },
            confirmButton = {
                Button(onClick = {
                    onPaymentSuccess()
                    onDismiss()
                }) {
                    Text("OK")
                }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF94A3B8)
        )
    }

    // Colors
    val darkBackground = Color(0xFF0F172A)
    val textPrimary = Color(0xFFFFFFFF)
    val textSecondary = Color(0xFF94A3B8)
    val blueAccent = Color(0xFF3B82F6)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRecruiter) "Approve & Pay" else "Mark as Complete",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textSecondary
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = blueAccent)
                }
            } else {
                val currentMission = mission
                if (currentMission != null) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Mission Title Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Mission",
                                    fontSize = 14.sp,
                                    color = textSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentMission.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentMission.formattedBudget,
                                    fontSize = 16.sp,
                                    color = blueAccent,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Payment Breakdown (Recruiter only) or Summary (Talent)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Payment Summary",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                val amount = currentMission.budget?.toDouble() ?: 0.0
                                val feePercent = 0.03 // 3% fee
                                val fee = amount * feePercent
                                val total = if (isRecruiter) amount + fee else amount - fee
                                
                                PaymentRow(
                                    label = "Mission Amount",
                                    value = formatCurrency(amount),
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                                
                                PaymentRow(
                                    label = "Platform Fee (3%)",
                                    value = formatCurrency(fee),
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFF334155)
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isRecruiter) "Total to Pay" else "You Receive",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Text(
                                        text = formatCurrency(total),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = blueAccent
                                    )
                                }
                            }
                        }

                        if (isRecruiter) {
                            // Payment Method placeholder (using default card)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Payment Method",
                                        fontSize = 14.sp,
                                        color = textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = blueAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Visa ending in 4242", // Placeholder
                                            fontSize = 16.sp,
                                            color = textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Error Message
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFEF4444),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // TEMPORARILY DISABLED STRIPE - TESTING IF IT'S THE CRASH SOURCE
                    /*
                    val paymentSheet = com.stripe.android.paymentsheet.rememberPaymentSheet { result ->
                        if (result is com.stripe.android.paymentsheet.PaymentSheetResult.Completed) {
                            viewModel.onPaymentSuccess()
                        } else if (result is com.stripe.android.paymentsheet.PaymentSheetResult.Failed) {
                            // Error message is handled in VM or shown here
                        }
                    }
                    */
                    
                    // Test state for mock payment
                    val showMockPayment = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    
                    if (showMockPayment.value) {
                        AlertDialog(
                            onDismissRequest = { showMockPayment.value = false },
                            title = { Text("TEST: Payment Would Happen Here") },
                            text = { Text("This is a placeholder. Click OK to simulate successful payment.") },
                            confirmButton = {
                                Button(onClick = {
                                    showMockPayment.value = false
                                    viewModel.onPaymentSuccess()
                                }) {
                                    Text("OK - Simulate Success")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showMockPayment.value = false }) {
                                    Text("Cancel")
                                }
                            },
                            containerColor = Color(0xFF1E293B),
                            titleContentColor = Color.White,
                            textContentColor = Color(0xFF94A3B8)
                        )
                    }

                    val paymentConfig by viewModel.paymentConfig.collectAsState()
                    val biometricAuthenticator = androidx.compose.runtime.remember { com.example.matchify.util.BiometricAuthenticator(context) }

                    // DO NOT auto-present - let button handle it explicitly

                    Button(
                        onClick = {
                            android.util.Log.d("MissionPaymentScreen", "Button clicked - MOCK VERSION")
                            try {
                                if (isRecruiter) {
                                    val config = paymentConfig
                                    if (config != null) {
                                        // Show mock payment instead of Stripe
                                        android.util.Log.d("MissionPaymentScreen", "Showing MOCK payment dialog")
                                        showMockPayment.value = true
                                    } else {
                                        // Fetch config
                                        android.util.Log.d("MissionPaymentScreen", "Fetching payment config...")
                                        viewModel.initiatePayment()
                                    }
                                } else {
                                    viewModel.completeMission()
                                }
                            } catch (e: Throwable) {
                                android.util.Log.e("MissionPaymentScreen", "Button error", e)
                                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isProcessing && mission?.status != "paid",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = blueAccent,
                            contentColor = Color.White
                        )
                    ) {
                        if (isProcessing || isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = when {
                                    mission?.status == "paid" -> "Paid"
                                    isRecruiter -> if (paymentConfig != null) "Complete Payment" else "Approve & Pay"
                                    else -> "Mark as Complete"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentRow(
    label: String,
    value: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = textSecondary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary
        )
    }
}

fun formatCurrency(amount: Double): String {
    // Simple formatter, in real app use NumberFormat
    return "$${String.format("%.2f", amount)}"
}

private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}
