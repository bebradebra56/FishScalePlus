package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fishscal.plisfo.data.model.FishCatch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    allCatches: List<FishCatch>,
    weightUnit: String,
    onNavigateBack: () -> Unit,
    onCatchClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { 
        mutableStateOf(Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        })
    }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    
    val dateFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH) }
    
    // Group catches by day
    val catchesByDay = remember(allCatches) {
        allCatches.groupBy { catch ->
            Calendar.getInstance().apply {
                timeInMillis = catch.date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
    
    val selectedDayCatches = selectedDate?.let { catchesByDay[it] } ?: emptyList()
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Calendar") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        // Month selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = Calendar.getInstance().apply {
                        timeInMillis = currentMonth.timeInMillis
                        add(Calendar.MONTH, -1)
                    }
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
                }
                
                Text(
                    text = dateFormat.format(currentMonth.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = {
                    currentMonth = Calendar.getInstance().apply {
                        timeInMillis = currentMonth.timeInMillis
                        add(Calendar.MONTH, 1)
                    }
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
                }
            }
        }
        
        // Calendar grid
        CalendarGrid(
            currentMonth = currentMonth,
            catchesByDay = catchesByDay,
            selectedDate = selectedDate,
            onDateClick = { date -> selectedDate = if (selectedDate == date) null else date },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // Selected day catches
        if (selectedDayCatches.isNotEmpty()) {
            Text(
                text = "Catches on ${SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(selectedDate!!))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedDayCatches, key = { it.id }) { catch ->
                    CatchListItem(
                        catch = catch,
                        weightUnit = weightUnit,
                        onClick = { onCatchClick(catch.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Calendar,
    catchesByDay: Map<Long, List<FishCatch>>,
    selectedDate: Long?,
    onDateClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = Calendar.getInstance().apply {
        timeInMillis = currentMonth.timeInMillis
        set(Calendar.DAY_OF_MONTH, 1)
    }.get(Calendar.DAY_OF_WEEK) - 1
    
    Column(modifier = modifier) {
        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar days
        var dayCounter = 1
        for (week in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (week == 0 && dayOfWeek < firstDayOfWeek) {
                            // Empty cell before month starts
                        } else if (dayCounter <= daysInMonth) {
                            val date = Calendar.getInstance().apply {
                                timeInMillis = currentMonth.timeInMillis
                                set(Calendar.DAY_OF_MONTH, dayCounter)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.timeInMillis
                            
                            val hasCatches = catchesByDay.containsKey(date)
                            val isSelected = selectedDate == date
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            hasCatches -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                                    )
                                    .clickable { onDateClick(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayCounter.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        hasCatches -> MaterialTheme.colorScheme.onSecondary
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (hasCatches || isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            dayCounter++
                        }
                    }
                }
            }
            if (dayCounter > daysInMonth) break
        }
    }
}

