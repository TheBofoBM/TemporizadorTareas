package com.example.temporizadorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // ¡Importante!
import androidx.compose.runtime.Composable
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

    private val timerViewModel: TimerViewModel by viewModels {
        TimerViewModel.Factory((application as TimerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemporizadorAppTheme {
                // 2. Le pasamos el ViewModel ya creado a la navegación
                MainNavigation(timerViewModel = timerViewModel)
            }
        }
    }
}

@Composable
fun MainNavigation(timerViewModel: TimerViewModel) {
    val navController = rememberNavController()

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