package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fishscal.plisfo.data.model.FishCatch
import com.fishscal.plisfo.data.model.defaultFishTypes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCatchScreen(
    existingCatch: FishCatch?,
    weightUnit: String,
    onSave: (FishCatch) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Convert existing weight to display unit if needed
    val initialWeight = remember(existingCatch, weightUnit) {
        if (existingCatch != null && weightUnit == "lb") {
            (existingCatch.weight * 2.20462f).toString()
        } else {
            existingCatch?.weight?.toString() ?: ""
        }
    }
    
    var selectedFishType by remember { mutableStateOf(existingCatch?.fishType ?: defaultFishTypes[0].name) }
    var weight by remember { mutableStateOf(initialWeight) }
    var length by remember { mutableStateOf(existingCatch?.length?.toString() ?: "") }
    var date by remember { mutableStateOf(existingCatch?.date ?: System.currentTimeMillis()) }
    var notes by remember { mutableStateOf(existingCatch?.notes ?: "") }
    var location by remember { mutableStateOf(existingCatch?.location ?: "") }
    var weather by remember { mutableStateOf(existingCatch?.weather ?: "") }
    var bait by remember { mutableStateOf(existingCatch?.bait ?: "") }
    
    var showFishTypeDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }
    
    // Date picker state
    val calendar = remember { Calendar.getInstance() }
    calendar.timeInMillis = date
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
            title = { Text(if (existingCatch != null) "Edit Catch" else "Add Catch") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fish Type
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showFishTypeDialog = true }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fish Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = selectedFishType,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight ($weightUnit)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    if (weight.isNotBlank() && weight.toFloatOrNull() == null) {
                        Text("Please enter a valid number")
                    }
                }
            )
            
            // Length (optional)
            OutlinedTextField(
                value = length,
                onValueChange = { length = it },
                label = { Text("Length (cm) - Optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Date
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = dateFormat.format(Date(date)),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            }
            
            // Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location - Optional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Weather
            OutlinedTextField(
                value = weather,
                onValueChange = { weather = it },
                label = { Text("Weather - Optional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Bait
            OutlinedTextField(
                value = bait,
                onValueChange = { bait = it },
                label = { Text("Bait - Optional") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes - Optional") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            // Save button
            Button(
                onClick = {
                    val weightValue = weight.toFloatOrNull()
                    if (weightValue != null && weightValue > 0) {
                        // Convert weight to kg for storage if unit is lb
                        val weightInKg = if (weightUnit == "lb") {
                            weightValue / 2.20462f
                        } else {
                            weightValue
                        }
                        
                        val catch = FishCatch(
                            id = existingCatch?.id ?: 0,
                            fishType = selectedFishType,
                            weight = weightInKg,
                            length = length.toFloatOrNull(),
                            date = date,
                            notes = notes.ifBlank { null },
                            location = location.ifBlank { null },
                            weather = weather.ifBlank { null },
                            bait = bait.ifBlank { null }
                        )
                        onSave(catch)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = weight.toFloatOrNull() != null && weight.toFloatOrNull()!! > 0
            ) {
                Text("Save Catch", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
    
    // Fish Type Dialog
    if (showFishTypeDialog) {
        AlertDialog(
            onDismissRequest = { showFishTypeDialog = false },
            title = { Text("Select Fish Type") },
            text = {
                Column {
                    defaultFishTypes.forEach { fishType ->
                        TextButton(
                            onClick = {
                                selectedFishType = fishType.name
                                showFishTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(fishType.icon, modifier = Modifier.padding(end = 12.dp))
                                Text(fishType.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            date = it
                            calendar.timeInMillis = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

