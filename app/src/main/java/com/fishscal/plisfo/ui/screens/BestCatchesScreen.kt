package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fishscal.plisfo.data.model.FishCatch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BestCatchesScreen(
    heaviestCatch: FishCatch?,
    longestCatch: FishCatch?,
    bestDay: Pair<Long, List<FishCatch>>?,
    weightUnit: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) }
    
    val heaviestWeight = if (weightUnit == "lb" && heaviestCatch != null)
        heaviestCatch.weight * 2.20462f
    else
        heaviestCatch?.weight
    
    val bestDayTotalWeight = if (weightUnit == "lb" && bestDay != null)
        bestDay.second.sumOf { it.weight.toDouble() } * 2.20462
    else
        bestDay?.second?.sumOf { it.weight.toDouble() }
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Best Catches") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heaviest catch
            if (heaviestCatch != null && heaviestWeight != null) {
                BestCatchCard(
                    title = "Heaviest Catch",
                    icon = "🏆",
                    mainValue = "${String.format("%.2f", heaviestWeight)} $weightUnit",
                    subtitle = heaviestCatch.fishType,
                    date = dateFormat.format(Date(heaviestCatch.date))
                )
            }
            
            // Longest catch
            if (longestCatch != null && longestCatch.length != null) {
                BestCatchCard(
                    title = "Longest Catch",
                    icon = "📏",
                    mainValue = "${longestCatch.length} cm",
                    subtitle = longestCatch.fishType,
                    date = dateFormat.format(Date(longestCatch.date))
                )
            }
            
            // Best day
            if (bestDay != null && bestDayTotalWeight != null) {
                BestCatchCard(
                    title = "Best Day",
                    icon = "🎣",
                    mainValue = "${bestDay.second.size} catches",
                    subtitle = "${String.format("%.2f", bestDayTotalWeight)} $weightUnit total",
                    date = dateFormat.format(Date(bestDay.first))
                )
            }
            
            // Empty state
            if (heaviestCatch == null && longestCatch == null && bestDay == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎯",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "No records yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Start catching to see your bests!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BestCatchCard(
    title: String,
    icon: String,
    mainValue: String,
    subtitle: String,
    date: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = mainValue,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

