package com.fishscal.plisfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.fishscal.plisfo.trng.presentation.app.FishScalePlusApplication
import com.fishscal.plisfo.ui.navigation.AppNavigation
import com.fishscal.plisfo.ui.theme.FishScalePlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val app = application as FishScalePlusApplication
        
        setContent {
            FishScalePlusTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    app = app
                )
            }
        }
    }
}