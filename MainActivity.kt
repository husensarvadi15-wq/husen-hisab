package com.sarvadiverse.husenhisab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sarvadiverse.husenhisab.ui.theme.HusenHisabTheme
import com.sarvadiverse.husenhisab.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        setContent {
            HusenHisabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToWork = { navController.navigate("daily_work") },
                                onNavigateToLedger = { navController.navigate("ledger") },
                                onNavigateToReports = { navController.navigate("reports") },
                                onLogout = { /* લૉગિન હટાવેલ હોવાથી ખાલી રાખ્યું છે */ }
                            )
                        }
                        composable("daily_work") {
                            DailyWorkScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("ledger") {
                            LedgerScreen(
                                viewModel = viewModel,
                                isLargePole = false,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("reports") {
                            ReportsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
