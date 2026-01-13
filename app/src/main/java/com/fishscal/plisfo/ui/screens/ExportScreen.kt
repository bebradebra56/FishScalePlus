package com.fishscal.plisfo.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.fishscal.plisfo.data.model.FishCatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    onExport: suspend (Long, Long) -> List<FishCatch>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }
    
    var startDate by remember { 
        mutableStateOf(
            Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var endDate by remember { 
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        )
    }
    
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Export Data") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Export your catches to CSV format",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            // Date range selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Date Range",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = dateFormat.format(Date(startDate)),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "To",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = dateFormat.format(Date(endDate)),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Quick select buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                endDate = cal.timeInMillis
                                cal.add(Calendar.MONTH, -1)
                                startDate = cal.timeInMillis
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Last Month")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                endDate = cal.timeInMillis
                                cal.add(Calendar.YEAR, -1)
                                startDate = cal.timeInMillis
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Last Year")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                endDate = System.currentTimeMillis()
                                startDate = 0
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("All Time")
                        }
                    }
                }
            }
            
            // Export button
            Button(
                onClick = {
                    scope.launch {
                        isExporting = true
                        try {
                            val catches = onExport(startDate, endDate)
                            if (catches.isNotEmpty()) {
                                exportToCsv(context, catches)
                                exportMessage = "Exported ${catches.size} catches successfully!"
                            } else {
                                exportMessage = "No catches found in selected date range"
                            }
                        } catch (e: Exception) {
                            exportMessage = "Export failed: ${e.message}"
                        } finally {
                            isExporting = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isExporting
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Export to CSV")
                }
            }
            
            // Export message
            if (exportMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = exportMessage!!,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private suspend fun exportToCsv(context: Context, catches: List<FishCatch>) {
    withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ENGLISH)
        val fileName = "fish_catches_${dateFormat.format(Date())}.csv"
        val file = File(context.cacheDir, fileName)
        
        file.bufferedWriter().use { writer ->
            // Header
            writer.write("Date,Fish Type,Weight (kg),Length (cm),Location,Weather,Bait,Notes\n")
            
            // Data
            val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH)
            catches.forEach { catch ->
                writer.write(
                    "${dataFormat.format(Date(catch.date))}," +
                    "\"${catch.fishType}\"," +
                    "${catch.weight}," +
                    "${catch.length ?: ""}," +
                    "\"${catch.location ?: ""}\"," +
                    "\"${catch.weather ?: ""}\"," +
                    "\"${catch.bait ?: ""}\"," +
                    "\"${catch.notes ?: ""}\"\n"
                )
            }
        }
        
        // Share the file
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share CSV"))
    }
}

