package com.fishscal.plisfo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fishscal.plisfo.data.model.FishCatch
import com.fishscal.plisfo.ui.navigation.BottomNavScreen

@Composable
fun MainScreen(
    allCatches: List<FishCatch>,
    filteredCatches: List<FishCatch>,
    catchesByType: Map<String, List<FishCatch>>,
    totalCatches: Int,
    averageWeight: Float?,
    maxWeight: Float?,
    uniqueFishTypes: Int,
    weightUnit: String,
    selectedFilter: String?,
    onFilterChange: (String?) -> Unit,
    onFishTypeClick: (String) -> Unit,
    onAddCatchClick: () -> Unit,
    onCatchClick: (Long) -> Unit,
    onBestCatchesClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onExportClick: () -> Unit,
    onWeightUnitChange: (String) -> Unit,
    onResetData: () -> Unit
) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute == BottomNavScreen.Log.route) {
                FloatingActionButton(
                    onClick = onAddCatchClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Catch")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Log.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavScreen.Log.route) {
                LogScreen(
                    catches = filteredCatches,
                    selectedFilter = selectedFilter,
                    weightUnit = weightUnit,
                    onFilterChange = onFilterChange,
                    onCatchClick = onCatchClick
                )
            }
            
            composable(BottomNavScreen.Fish.route) {
                FishScreen(
                    catchesByType = catchesByType,
                    weightUnit = weightUnit,
                    onFishTypeClick = onFishTypeClick
                )
            }
            
            composable(BottomNavScreen.Stats.route) {
                StatsScreen(
                    totalCatches = totalCatches,
                    averageWeight = averageWeight,
                    maxWeight = maxWeight,
                    uniqueFishTypes = uniqueFishTypes,
                    weightUnit = weightUnit,
                    allCatches = allCatches
                )
            }
            
            composable(BottomNavScreen.Settings.route) {
                SettingsScreen(
                    weightUnit = weightUnit,
                    onWeightUnitChange = onWeightUnitChange,
                    onResetData = onResetData,
                    onBestCatchesClick = onBestCatchesClick,
                    onCalendarClick = onCalendarClick,
                    onExportClick = onExportClick
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            screen = BottomNavScreen.Log,
            icon = Icons.Default.List,
            label = "Log"
        ),
        BottomNavItem(
            screen = BottomNavScreen.Fish,
            icon = Icons.Default.Phishing,
            label = "Fish"
        ),
        BottomNavItem(
            screen = BottomNavScreen.Stats,
            icon = Icons.Default.Analytics,
            label = "Stats"
        ),
        BottomNavItem(
            screen = BottomNavScreen.Settings,
            icon = Icons.Default.Settings,
            label = "Settings"
        )
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val screen: BottomNavScreen,
    val icon: ImageVector,
    val label: String
)

