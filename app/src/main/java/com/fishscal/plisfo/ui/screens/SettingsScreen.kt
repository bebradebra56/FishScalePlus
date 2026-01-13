package com.fishscal.plisfo.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    weightUnit: String,
    onWeightUnitChange: (String) -> Unit,
    onResetData: () -> Unit,
    onBestCatchesClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings") }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Features section
            Text(
                text = "Features",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsItem(
                title = "Best Catches",
                subtitle = "View your record catches",
                icon = Icons.Default.Star,
                onClick = onBestCatchesClick
            )
            
            SettingsItem(
                title = "Calendar",
                subtitle = "View catches by date",
                icon = Icons.Default.CalendarToday,
                onClick = onCalendarClick
            )
            
            SettingsItem(
                title = "Export Data",
                subtitle = "Export catches to CSV",
                icon = Icons.Default.FileDownload,
                onClick = onExportClick
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Preferences section
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsItem(
                title = "Units",
                subtitle = "Weight: $weightUnit",
                icon = Icons.Default.Scale,
                onClick = { showUnitDialog = true }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Data section
            Text(
                text = "Data",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsItem(
                title = "Reset Data",
                subtitle = "Delete all catches and settings",
                icon = Icons.Default.Delete,
                onClick = { showResetDialog = true },
                isDestructive = true
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // About section
            Text(
                text = "About",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            SettingsItem(
                title = "Privacy Policy",
                subtitle = "Tap to read",
                icon = Icons.Default.PrivacyTip,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://fishscaleplus.com/privacy-policy.html"))
                    context.startActivity(intent)
                }
            )
        }
    }
    
    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Reset All Data?") },
            text = { Text("This will permanently delete all your catches and reset settings. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Unit selection dialog
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = { Text("Select Weight Unit") },
            text = {
                Column {
                    listOf("kg", "lb").forEach { unit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onWeightUnitChange(unit)
                                    showUnitDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = weightUnit == unit,
                                onClick = {
                                    onWeightUnitChange(unit)
                                    showUnitDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(unit)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

