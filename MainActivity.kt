package com.sarvadiverse.husenhisab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.sarvadiverse.husenhisab.ui.screens.*
import com.sarvadiverse.husenhisab.ui.theme.HusenHisabTheme
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HusenHisabTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val auth = FirebaseAuth.getInstance()
                    val startDestination = if (auth.currentUser != null) "dashboard" else "login"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    viewModel.loadAllData()
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToWork = { navController.navigate("work_entry") },
                                onNavigateToLedger = { isLarge -> navController.navigate("ledger/$isLarge") },
                                onNavigateToReports = { navController.navigate("reports") },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("work_entry") {
                            DailyWorkScreen(viewModel = viewModel)
                        }
                        composable("ledger/{isLarge}") { backStackEntry ->
                            val isLarge = backStackEntry.arguments?.getString("isLarge")?.toBoolean() ?: false
                            LedgerScreen(isLargePole = isLarge, viewModel = viewModel)
                        }
                        composable("reports") {
                            ReportsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
