package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fishscal.plisfo.data.model.FishCatch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    totalCatches: Int,
    averageWeight: Float?,
    maxWeight: Float?,
    uniqueFishTypes: Int,
    weightUnit: String,
    allCatches: List<FishCatch>,
    modifier: Modifier = Modifier
) {
    val displayAvgWeight = if (weightUnit == "lb" && averageWeight != null) 
        averageWeight * 2.20462f 
    else 
        averageWeight
    val displayMaxWeight = if (weightUnit == "lb" && maxWeight != null) 
        maxWeight * 2.20462f 
    else 
        maxWeight
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Statistics") }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total Catches",
                    value = totalCatches.toString(),
                    icon = "🎣",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Fish Types",
                    value = uniqueFishTypes.toString(),
                    icon = "🐠",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Avg Weight",
                    value = if (displayAvgWeight != null) "${String.format("%.2f", displayAvgWeight)} $weightUnit" else "0 $weightUnit",
                    icon = "⚖️",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Max Weight",
                    value = if (displayMaxWeight != null) "${String.format("%.2f", displayMaxWeight)} $weightUnit" else "0 $weightUnit",
                    icon = "🏆",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Weight over time chart
            if (allCatches.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Weight Over Time",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        WeightChart(catches = allCatches.sortedBy { it.date })
                    }
                }
                
                // Monthly catches chart
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Catches by Month",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        MonthlyCatchesChart(catches = allCatches)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun WeightChart(catches: List<FishCatch>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(bottom = 20.dp)
        ) {
            if (catches.isEmpty()) return@Canvas
            
            val maxWeight = catches.maxOf { it.weight }
            val minWeight = catches.minOf { it.weight }
            val weightRange = if (maxWeight == minWeight) maxWeight * 0.2f else maxWeight - minWeight
            
            val padding = 60f
            val bottomPadding = 40f
            val chartWidth = size.width - padding * 2
            val chartHeight = size.height - padding - bottomPadding
            
            // Draw axes
            drawLine(
                color = gridColor,
                start = Offset(padding, padding),
                end = Offset(padding, size.height - bottomPadding),
                strokeWidth = 2f
            )
            drawLine(
                color = gridColor,
                start = Offset(padding, size.height - bottomPadding),
                end = Offset(size.width - padding, size.height - bottomPadding),
                strokeWidth = 2f
            )
            
            // Draw grid lines and labels
            for (i in 0..4) {
                val y = padding + (chartHeight / 4) * i
                val weight = maxWeight - (weightRange / 4 * i)
                
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(size.width - padding, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        String.format("%.1f", weight),
                        padding - 40f,
                        y + 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }
            
            // Draw line chart
            if (catches.size > 1) {
                val xStep = chartWidth / (catches.size - 1)
                
                // Draw gradient area under line
                val path = androidx.compose.ui.graphics.Path()
                catches.forEachIndexed { index, catch ->
                    val x = padding + index * xStep
                    val normalizedWeight = if (weightRange > 0) (catch.weight - minWeight) / weightRange else 0.5f
                    val y = size.height - bottomPadding - (normalizedWeight * chartHeight)
                    
                    if (index == 0) {
                        path.moveTo(x, size.height - bottomPadding)
                        path.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                path.lineTo(padding + (catches.size - 1) * xStep, size.height - bottomPadding)
                path.close()
                
                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.2f)
                )
                
                // Draw line
                for (i in 0 until catches.size - 1) {
                    val x1 = padding + i * xStep
                    val normalizedWeight1 = if (weightRange > 0) (catches[i].weight - minWeight) / weightRange else 0.5f
                    val y1 = size.height - bottomPadding - (normalizedWeight1 * chartHeight)
                    
                    val x2 = padding + (i + 1) * xStep
                    val normalizedWeight2 = if (weightRange > 0) (catches[i + 1].weight - minWeight) / weightRange else 0.5f
                    val y2 = size.height - bottomPadding - (normalizedWeight2 * chartHeight)
                    
                    drawLine(
                        color = primaryColor,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = 5f
                    )
                }
                
                // Draw points
                catches.forEachIndexed { index, catch ->
                    val x = padding + index * xStep
                    val normalizedWeight = if (weightRange > 0) (catch.weight - minWeight) / weightRange else 0.5f
                    val y = size.height - bottomPadding - (normalizedWeight * chartHeight)
                    
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.White,
                        radius = 10f,
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 7f,
                        center = Offset(x, y)
                    )
                }
            } else if (catches.size == 1) {
                // Single point
                val x = padding + chartWidth / 2
                val normalizedWeight = 0.5f
                val y = size.height - bottomPadding - (normalizedWeight * chartHeight)
                
                drawCircle(
                    color = androidx.compose.ui.graphics.Color.White,
                    radius = 10f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = primaryColor,
                    radius = 7f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun MonthlyCatchesChart(catches: List<FishCatch>) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val dateFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
    
    // Group by year-month for better organization
    val monthlyData = catches.groupBy { catch ->
        val cal = Calendar.getInstance().apply { timeInMillis = catch.date }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
    }.mapKeys { entry ->
        val parts = entry.key.split("-")
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, parts[0].toInt())
            set(Calendar.MONTH, parts[1].toInt())
        }
        dateFormat.format(cal.time)
    }.mapValues { it.value.size }
    
    // Take last 6 months if there are more
    val displayData = if (monthlyData.size > 6) {
        monthlyData.entries.toList().takeLast(6).associate { it.key to it.value }
    } else {
        monthlyData
    }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(bottom = 20.dp)
        ) {
            if (displayData.isEmpty()) return@Canvas
            
            val maxCatches = displayData.values.maxOrNull() ?: 1
            val padding = 60f
            val bottomPadding = 60f
            val chartWidth = size.width - padding * 2
            val chartHeight = size.height - padding - bottomPadding
            
            // Draw axes
            drawLine(
                color = gridColor,
                start = Offset(padding, padding),
                end = Offset(padding, size.height - bottomPadding),
                strokeWidth = 2f
            )
            drawLine(
                color = gridColor,
                start = Offset(padding, size.height - bottomPadding),
                end = Offset(size.width - padding, size.height - bottomPadding),
                strokeWidth = 2f
            )
            
            // Draw grid lines
            for (i in 0..4) {
                val y = padding + (chartHeight / 4) * i
                val count = maxCatches - (maxCatches / 4 * i)
                
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(size.width - padding, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        count.toString(),
                        padding - 30f,
                        y + 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }
            
            val barWidth = chartWidth / (displayData.size * 1.5f)
            val spacing = barWidth * 0.5f
            
            displayData.entries.forEachIndexed { index, entry ->
                val barHeight = (entry.value.toFloat() / maxCatches) * chartHeight
                val x = padding + index * (barWidth + spacing)
                val y = size.height - bottomPadding - barHeight
                
                // Draw bar with gradient
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
                
                // Draw value on top of bar
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        entry.value.toString(),
                        x + barWidth / 2,
                        y - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
                
                // Draw month label
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawText(
                        entry.key,
                        x + barWidth / 2,
                        size.height - bottomPadding + 30f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 26f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}

