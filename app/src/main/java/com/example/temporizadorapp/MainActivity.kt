package com.example.temporizadorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.temporizadorapp.ui.screens.configuration.TemplatesScreen
import com.example.temporizadorapp.ui.screens.history.HistoryScreen
import com.example.temporizadorapp.ui.screens.timer.SessionScreen
import com.example.temporizadorapp.ui.screens.timer.TasksScreen
import com.example.temporizadorapp.ui.screens.timer.TimerScreen
import com.example.temporizadorapp.ui.theme.TemporizadorAppTheme
import com.example.temporizadorapp.ui.viewmodel.TimerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemporizadorAppTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val timerViewModel: TimerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "timer") {
        composable("timer") {
            TimerScreen(
                viewModel = timerViewModel,
                onStartSession = { navController.navigate("session") },
                onNavigateToTemplates = { navController.navigate("templates") },
                onNavigateToTasks = { navController.navigate("tasks") },
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        composable("templates") {
            TemplatesScreen(
                viewModel = timerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("tasks") {
            TasksScreen(
                viewModel = timerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("session") {
            SessionScreen(
                viewModel = timerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
