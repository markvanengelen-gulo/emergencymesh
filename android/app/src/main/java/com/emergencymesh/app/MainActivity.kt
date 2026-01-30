package com.emergencymesh.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emergencymesh.app.ui.screens.HomeScreen
import com.emergencymesh.app.ui.screens.OnboardingScreen
import com.emergencymesh.app.ui.screens.SettingsScreen
import com.emergencymesh.app.ui.theme.EmergencyMeshTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EmergencyMeshTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmergencyMeshApp()
                }
            }
        }
    }
}

@Composable
fun EmergencyMeshApp() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Check if onboarding is complete
    val prefs = remember {
        context.getSharedPreferences("emergency_mesh_prefs", ComponentActivity.MODE_PRIVATE)
    }
    val isOnboardingComplete = prefs.getBoolean("onboarding_complete", false)
    val startDestination = if (isOnboardingComplete) "home" else "onboarding"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
