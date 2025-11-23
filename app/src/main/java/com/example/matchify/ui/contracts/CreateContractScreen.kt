package com.example.matchify.ui.contracts

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

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
    val content by viewModel.content.collectAsState()
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nouveau contrat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF007AFF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF2F2F2)
                ),
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.createContract(missionId, talentId) {
                                // Contract created - LaunchedEffect will handle closing
                            }
                        },
                        enabled = title.isNotEmpty() && 
                                 content.isNotEmpty() && 
                                 signatureBitmap != null && 
                                 !isLoading
                    ) {
                        Text("Send")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Titre du contrat") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            // Content
            OutlinedTextField(
                value = content,
                onValueChange = { viewModel.content.value = it },
                label = { Text("Termes du contrat") },
                minLines = 5,
                maxLines = 10,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            // Payment Details
            OutlinedTextField(
                value = paymentDetails,
                onValueChange = { viewModel.paymentDetails.value = it },
                label = { Text("Détails de paiement (optionnel)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            // Start Date
            OutlinedTextField(
                value = startDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Date de début (optionnel)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = "Select date")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            // End Date
            OutlinedTextField(
                value = endDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Date de fin (optionnel)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = "Select date")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            // Signature Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Signature",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    if (signatureBitmap != null) {
                        Image(
                            bitmap = signatureBitmap!!.asImageBitmap(),
                            contentDescription = "Signature",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        )
                    }
                    
                    Button(
                        onClick = { showSignaturePad = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Signer")
                    }
                }
            }
            
            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    
    // Date Pickers
    if (showStartDatePicker) {
        DatePickerDialog(
            initialDate = startDate ?: System.currentTimeMillis(),
            onDateSelected = { timestamp ->
                viewModel.startDate.value = timestamp
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            initialDate = endDate ?: System.currentTimeMillis(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // Material Design 3 DatePicker automatically uses system locale
    // This ensures weekday headers (M, T, W, T, F, S, S) display correctly
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
    )
    
    val selectedDate = datePickerState.selectedDateMillis
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner une date") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Material Design 3 DatePicker - automatically handles locale and weekday display
                // Weekday headers will display correctly according to system locale
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedDate?.let {
                        onDateSelected(it)
                    } ?: run {
                        // If no date selected, use initial date
                        onDateSelected(initialDate)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

