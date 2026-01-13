package com.fishscal.plisfo.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fishscal.plisfo.data.model.FishCatch
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.fishscal.plisfo.ui.screens.*
import com.fishscal.plisfo.ui.viewmodel.FishViewModel
import com.fishscal.plisfo.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController,
    app: FishScalePlusApplication,
    viewModel: FishViewModel = viewModel(
        factory = ViewModelFactory(app.repository, app.preferencesManager)
    )
) {
    val scope = rememberCoroutineScope()
    
    // Collect states
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
    val allCatches by viewModel.allCatches.collectAsState()
    val filteredCatches by viewModel.filteredCatches.collectAsState()
    val catchesByType by viewModel.catchesByType.collectAsState()
    val totalCatches by viewModel.totalCatches.collectAsState()
    val averageWeight by viewModel.averageWeight.collectAsState()
    val maxWeight by viewModel.maxWeight.collectAsState()
    val uniqueFishTypes by viewModel.uniqueFishTypes.collectAsState()
    val weightUnit by viewModel.weightUnit.collectAsState()
    val selectedFilter by viewModel.selectedFishType.collectAsState()
    val heaviestCatch by viewModel.heaviestCatch.collectAsState()
    val longestCatch by viewModel.longestCatch.collectAsState()
    val bestDay by viewModel.bestDay.collectAsState()
    
    // Determine start destination
    val startDestination = when {
        !isOnboardingCompleted -> Screen.Splash.route
        else -> Screen.Main.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToNext = {
                    navController.navigate(
                        if (isOnboardingCompleted) Screen.Main.route else Screen.Onboarding.route
                    ) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main
        composable(Screen.Main.route) {
            MainScreen(
                allCatches = allCatches,
                filteredCatches = filteredCatches,
                catchesByType = catchesByType,
                totalCatches = totalCatches,
                averageWeight = averageWeight,
                maxWeight = maxWeight,
                uniqueFishTypes = uniqueFishTypes,
                weightUnit = weightUnit,
                selectedFilter = selectedFilter,
                onFilterChange = { viewModel.setFilterByFishType(it) },
                onFishTypeClick = { fishType ->
                    viewModel.setFilterByFishType(fishType)
                    // Navigate to log screen would require access to bottom nav controller
                },
                onAddCatchClick = {
                    navController.navigate(Screen.AddEditCatch.createRoute())
                },
                onCatchClick = { catchId ->
                    navController.navigate(Screen.CatchDetail.createRoute(catchId))
                },
                onBestCatchesClick = {
                    navController.navigate(Screen.BestCatches.route)
                },
                onCalendarClick = {
                    navController.navigate(Screen.Calendar.route)
                },
                onExportClick = {
                    navController.navigate(Screen.Export.route)
                },
                onWeightUnitChange = { viewModel.setWeightUnit(it) },
                onResetData = { viewModel.resetAllData() }
            )
        }
        
        // Add/Edit Catch
        composable(
            route = Screen.AddEditCatch.route,
            arguments = listOf(navArgument("catchId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val catchIdString = backStackEntry.arguments?.getString("catchId")
            val catchId = catchIdString?.toLongOrNull()
            
            var existingCatch by remember { mutableStateOf<FishCatch?>(null) }
            
            LaunchedEffect(catchId) {
                if (catchId != null) {
                    existingCatch = viewModel.getCatchById(catchId)
                }
            }
            
            AddEditCatchScreen(
                existingCatch = existingCatch,
                weightUnit = weightUnit,
                onSave = { catch ->
                    if (catch.id == 0L) {
                        viewModel.insertCatch(catch)
                    } else {
                        viewModel.updateCatch(catch)
                    }
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Catch Detail
        composable(
            route = Screen.CatchDetail.route,
            arguments = listOf(navArgument("catchId") { type = NavType.LongType })
        ) { backStackEntry ->
            val catchId = backStackEntry.arguments?.getLong("catchId") ?: return@composable
            
            var catch by remember { mutableStateOf<FishCatch?>(null) }
            
            LaunchedEffect(catchId) {
                catch = viewModel.getCatchById(catchId)
            }
            
            CatchDetailScreen(
                catch = catch,
                weightUnit = weightUnit,
                onNavigateBack = { navController.popBackStack() },
                onEdit = {
                    navController.navigate(Screen.AddEditCatch.createRoute(catchId))
                },
                onDelete = {
                    catch?.let { viewModel.deleteCatch(it) }
                    navController.popBackStack()
                }
            )
        }
        
        // Best Catches
        composable(Screen.BestCatches.route) {
            BestCatchesScreen(
                heaviestCatch = heaviestCatch,
                longestCatch = longestCatch,
                bestDay = bestDay,
                weightUnit = weightUnit,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Calendar
        composable(Screen.Calendar.route) {
            CalendarScreen(
                allCatches = allCatches,
                weightUnit = weightUnit,
                onNavigateBack = { navController.popBackStack() },
                onCatchClick = { catchId ->
                    navController.navigate(Screen.CatchDetail.createRoute(catchId))
                }
            )
        }
        
        // Export
        composable(Screen.Export.route) {
            ExportScreen(
                onNavigateBack = { navController.popBackStack() },
                onExport = { startDate, endDate ->
                    viewModel.getCatchesForExport(startDate, endDate)
                }
            )
        }
    }
}

