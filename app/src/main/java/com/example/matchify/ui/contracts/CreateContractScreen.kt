package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContractScreen(
    missionId: String,
    talentId: String,
    onBack: () -> Unit,
    onContractCreated: () -> Unit,
    viewModel: CreateContractViewModel = viewModel(factory = CreateContractViewModelFactory())
) {
    val title by viewModel.title.collectAsState()
    val scope by viewModel.scope.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val paymentDetails by viewModel.paymentDetails.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    val signatureBitmap by viewModel.signatureBitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showSignaturePad by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    LaunchedEffect(viewModel.contractCreated.value) {
        viewModel.contractCreated.value?.let {
            onContractCreated()
        }
    }
    
    // Screen background: #0F172A
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Header
            ContractHeader(
                onBack = onBack,
                onSend = {
                    viewModel.createContract(missionId, talentId) {
                        // Contract created - LaunchedEffect will handle closing
                    }
                },
                isSendEnabled = title.isNotEmpty() && 
                               scope.isNotEmpty() && 
                               budget.isNotEmpty() && 
                               signatureBitmap != null && 
                               !isLoading
            )
            
            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Field
                DarkTextField(
                    value = title,
                    onValueChange = { viewModel.title.value = it },
                    label = "Contract Title",
                    singleLine = true
                )
                
                // Scope Field (multi-line)
                DarkTextField(
                    value = scope,
                    onValueChange = { viewModel.scope.value = it },
                    label = "Project Scope and Deliverables",
                    singleLine = false,
                    minLines = 5
                )
                
                // Budget Field
                DarkTextField(
                    value = budget,
                    onValueChange = { viewModel.budget.value = it },
                    label = "Budget and Payment Terms",
                    singleLine = true
                )
                
                // Payment Details Field
                DarkTextField(
                    value = paymentDetails,
                    onValueChange = { viewModel.paymentDetails.value = it },
                    label = "Payment Details (optional)",
                    singleLine = true
                )
                
                // Start Date Field
                DarkDateField(
                    value = dateFormatter.format(Date(startDate)),
                    label = "Start Date",
                    onClick = { showStartDatePicker = true }
                )
                
                // End Date Field
                DarkDateField(
                    value = dateFormatter.format(Date(endDate)),
                    label = "End Date",
                    onClick = { showEndDatePicker = true }
                )
                
                // Signature Section
                SignatureSection(
                    signatureBitmap = signatureBitmap,
                    onSignClick = { showSignaturePad = true }
                )
                
                // Error Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
    
    // Date Pickers
    if (showStartDatePicker) {
        DarkDatePickerDialog(
            initialDate = startDate,
            onDateSelected = { timestamp ->
                viewModel.startDate.value = timestamp
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    if (showEndDatePicker) {
        DarkDatePickerDialog(
            initialDate = endDate,
            onDateSelected = { timestamp ->
                viewModel.endDate.value = timestamp
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
    
    // Signature Pad
    if (showSignaturePad) {
        SignaturePadView(
            onSignatureCaptured = { bitmap ->
                viewModel.setSignature(bitmap)
                showSignaturePad = false
            },
            onDismiss = { showSignaturePad = false }
        )
    }
}

// Custom Header Component
@Composable
private fun ContractHeader(
    onBack: () -> Unit,
    onSend: () -> Unit,
    isSendEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF1E293B))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(22.dp),
                tint = Color(0xFF3B82F6)
            )
        }
        
        // Title
        Text(
            text = "Create Contract",
            fontSize = 18.sp,
            fontWeight = FontWeight(600),
            color = Color.White
        )
        
        // Send Button
        TextButton(
            onClick = onSend,
            enabled = isSendEnabled
        ) {
            Text(
                text = "Send",
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = if (isSendEnabled) Color(0xFF3B82F6) else Color(0xFF9CA3AF)
            )
        }
    }
}

// Dark-themed Text Field Component
@Composable
private fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean,
    minLines: Int = 1
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF9CA3AF)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF374151))
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color.White
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = singleLine,
                minLines = minLines
            )
        }
    }
}

// Dark-themed Date Field Component
@Composable
private fun DarkDateField(
    value: String,
    label: String,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF9CA3AF)
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF374151))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifEmpty { "Select date" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = if (value.isEmpty()) Color(0xFF9CA3AF) else Color.White
                )
                
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Select date",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF3B82F6)
                )
            }
        }
    }
}

// Signature Section Component
@Composable
private fun SignatureSection(
    signatureBitmap: Bitmap?,
    onSignClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF374151))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Signature",
                fontSize = 16.sp,
                fontWeight = FontWeight(600),
                color = Color.White
            )
            
            if (signatureBitmap != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(
                        bitmap = signatureBitmap.asImageBitmap(),
                        contentDescription = "Signature",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Button(
                onClick = onSignClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "Sign",
                    fontSize = 15.sp,
                    fontWeight = FontWeight(600),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// Dark-themed Date Picker Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DarkDatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )
    
    val selectedDate = datePickerState.selectedDateMillis
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        title = { 
            Text(
                "Select Date",
                color = Color.White
            ) 
        },
        text = {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(0.dp)
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.wrapContentWidth(),
                    colors = DatePickerDefaults.colors(
                        containerColor = Color(0xFF1E293B),
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color(0xFF9CA3AF),
                        subheadContentColor = Color.White,
                        yearContentColor = Color.White,
                        currentYearContentColor = Color(0xFF3B82F6),
                        selectedYearContentColor = Color.White,
                        selectedYearContainerColor = Color(0xFF3B82F6),
                        dayContentColor = Color.White,
                        selectedDayContentColor = Color.White,
                        selectedDayContainerColor = Color(0xFF3B82F6),
                        todayContentColor = Color(0xFF3B82F6),
                        todayDateBorderColor = Color(0xFF3B82F6)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let {
                        onDateSelected(it)
                    } ?: run {
                        onDateSelected(initialDate)
                    }
                }
            ) {
                Text("OK", color = Color(0xFF3B82F6))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF9CA3AF))
            }
        }
    )
}

