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
import com.sarvadiverse.husenhisab.ui.theme.HusenHisabTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HusenHisabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // startDestination સીધું "dashboard" રાખ્યું છે (Login Screen બાયપાસ)
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        composable("dashboard") {
                            DashboardScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("daily_work") {
                            DailyWorkScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("ledger") {
                            LedgerScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("reports") {
                            ReportsScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
