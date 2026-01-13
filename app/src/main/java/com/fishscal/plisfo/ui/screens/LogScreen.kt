package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishscal.plisfo.data.model.FishCatch
import com.fishscal.plisfo.data.model.defaultFishTypes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    catches: List<FishCatch>,
    selectedFilter: String?,
    weightUnit: String,
    onFilterChange: (String?) -> Unit,
    onCatchClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Top bar
        TopAppBar(
            title = { Text("My Catches") },
            actions = {
                IconButton(onClick = { showFilterDialog = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            }
        )
        
        // Filter chip
        if (selectedFilter != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                FilterChip(
                    selected = true,
                    onClick = { onFilterChange(null) },
                    label = { Text(selectedFilter) },
                    trailingIcon = {
                        Text("✕", fontSize = 16.sp)
                    }
                )
            }
        }
        
        // Catches list
        if (catches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎣",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No catches yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Start logging your catches!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(catches, key = { it.id }) { catch ->
                    CatchListItem(
                        catch = catch,
                        weightUnit = weightUnit,
                        onClick = { onCatchClick(catch.id) }
                    )
                }
            }
        }
    }
    
    // Filter dialog
    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onFilterSelect = { fishType ->
                onFilterChange(fishType)
                showFilterDialog = false
            }
        )
    }
}

@Composable
fun CatchListItem(
    catch: FishCatch,
    weightUnit: String,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }
    val weight = if (weightUnit == "lb") catch.weight * 2.20462f else catch.weight
    val weightText = String.format("%.2f %s", weight, weightUnit)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fish icon
            Text(
                text = "🐟",
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = catch.fishType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weightText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                if (catch.length != null) {
                    Text(
                        text = "${catch.length} cm",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = dateFormat.format(Date(catch.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilterSelect: (String?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter by Fish Type") },
        text = {
            Column {
                TextButton(
                    onClick = { onFilterSelect(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("All Fish", modifier = Modifier.fillMaxWidth())
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                defaultFishTypes.forEach { fishType ->
                    TextButton(
                        onClick = { onFilterSelect(fishType.name) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(fishType.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(fishType.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

