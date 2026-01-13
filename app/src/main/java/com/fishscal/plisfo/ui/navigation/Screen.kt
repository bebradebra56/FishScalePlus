package com.fishscal.plisfo.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object CatchDetail : Screen("catch_detail/{catchId}") {
        fun createRoute(catchId: Long) = "catch_detail/$catchId"
    }
    object AddEditCatch : Screen("add_edit_catch?catchId={catchId}") {
        fun createRoute(catchId: Long? = null) = 
            if (catchId != null) "add_edit_catch?catchId=$catchId" else "add_edit_catch"
    }
    object BestCatches : Screen("best_catches")
    object Calendar : Screen("calendar")
    object Export : Screen("export")
}

sealed class BottomNavScreen(val route: String, val title: String) {
    object Log : BottomNavScreen("log", "Log")
    object Fish : BottomNavScreen("fish", "Fish")
    object Stats : BottomNavScreen("stats", "Stats")
    object Settings : BottomNavScreen("settings", "Settings")
}

