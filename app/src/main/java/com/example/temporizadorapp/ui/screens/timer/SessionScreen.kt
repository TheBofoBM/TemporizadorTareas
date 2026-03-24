package com.example.temporizadorapp.ui.screens.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temporizadorapp.ui.viewmodel.TimerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(viewModel: TimerViewModel, onBack: () -> Unit) {
    val config by viewModel.currentConfig.collectAsState()
    val taskName by viewModel.taskName.collectAsState()
    
    var timeLeft by remember { mutableIntStateOf(config.workTime * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var currentSet by remember { mutableIntStateOf(1) }
    var isMetronomeOn by remember { mutableStateOf(false) }

    // Timer logic
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        } else if (timeLeft == 0) {
            isRunning = false
            // Logic for next phase (break/next set) could go here
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Serie $currentSet de ${config.totalSets}", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF0F4FF))
            )
        },
        containerColor = Color(0xFFF0F4FF)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Progress Circle
                Canvas(modifier = Modifier.size(280.dp)) {
                    drawArc(
                        color = Color(0xFFE0E0E0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color(0xFF1D5DFF),
                        startAngle = -90f,
                        sweepAngle = (timeLeft.toFloat() / (config.workTime * 60)) * 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(timeLeft),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        "Tiempo de Trabajo",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pagination dots (simplified)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { i ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (i == 0) Color(0xFF1D5DFF) else Color(0xFFD1D1D1),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier.height(56.dp).width(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D5DFF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pausar" else "Iniciar", fontSize = 18.sp)
                }

                Surface(
                    onClick = { timeLeft = config.workTime * 60; isRunning = false },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", modifier = Modifier.padding(16.dp))
                }

                Surface(
                    onClick = { /* Skip */ },
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Siguiente", modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💪 Mantén tu concentración en la tarea", color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Metronome Control
            Surface(
                onClick = { isMetronomeOn = !isMetronomeOn },
                color = if (isMetronomeOn) Color(0xFFE3F2FD) else Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.MusicNote, contentDescription = null, tint = if (isMetronomeOn) Color(0xFF1D5DFF) else Color.Gray)
                    Text(
                        if (isMetronomeOn) "Metrónomo Activo (Pausar)" else "Activar Metrónomo",
                        modifier = Modifier.padding(start = 4.dp),
                        color = if (isMetronomeOn) Color(0xFF1D5DFF) else Color.Gray
                    )
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
