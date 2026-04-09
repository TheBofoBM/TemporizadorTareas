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

// 1. Creamos un Enum para manejar los estados y sus colores visuales
enum class SessionPhase(val title: String, val color: Color) {
    WORK("Tiempo de Trabajo", Color(0xFF1D5DFF)), // Azul original
    SHORT_BREAK("Descanso Corto", Color(0xFF00C853)), // Verde
    LONG_BREAK("Descanso Largo", Color(0xFF7B1FA2)) // Morado
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(viewModel: TimerViewModel, onBack: () -> Unit) {
    val config by viewModel.currentConfig.collectAsState()
    val taskName by viewModel.taskName.collectAsState()

    // 2. Variables de estado para el motor de fases
    var currentPhase by remember { mutableStateOf(SessionPhase.WORK) }
    var currentSet by remember { mutableIntStateOf(1) }
    var isRunning by remember { mutableStateOf(false) }
    var isMetronomeOn by remember { mutableStateOf(false) }

    // Dentro de SessionScreen
    var showFinishedDialog by remember { mutableStateOf(false) }

    // 3. Calculamos el tiempo total dependiendo de la fase actual
    val phaseTotalTime = remember(currentPhase, config) {
        when (currentPhase) {
            SessionPhase.WORK -> config.workTime * 60
            SessionPhase.SHORT_BREAK -> config.breakTime * 60
            SessionPhase.LONG_BREAK -> config.longBreakTime * 60
        }
    }

    // El tiempo restante se reinicia automáticamente cuando cambia la fase
    var timeLeft by remember(phaseTotalTime) { mutableIntStateOf(phaseTotalTime) }

    // 4. Función central para avanzar de fase
    fun advanceToNextPhase() {
        when (currentPhase) {
            SessionPhase.WORK -> {
                if (currentSet < config.totalSets) {
                    currentPhase = SessionPhase.SHORT_BREAK
                } else {
                    currentPhase = SessionPhase.LONG_BREAK
                }
            }
            SessionPhase.SHORT_BREAK -> {
                currentPhase = SessionPhase.WORK
                currentSet++
            }
            SessionPhase.LONG_BREAK -> {
                // --- FINALIZACIÓN DE SESIÓN ---
                isRunning = false
                viewModel.toggleTaskCompletion(viewModel.taskName.value, true) // Guardamos en DB
                showFinishedDialog = true // Mostramos el éxito al usuario
                return
            }
        }
        timeLeft = phaseTotalTime
        isRunning = config.autoStart
    }

    // Efecto para el sonido del metrónomo
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                // 1. Sonar metrónomo si está activo (Fuego y olvido)
                if (isMetronomeOn) {
                    val mp = android.media.MediaPlayer.create(context, com.example.temporizadorapp.R.raw.metronomo)
                    mp?.setVolume(0.3f, 0.3f)
                    mp?.start()
                    mp?.setOnCompletionListener { it.release() }
                }

                // 2. Esperar el segundo exacto
                delay(1000L)

                // 3. Restar el tiempo
                timeLeft -= 1
            }

            // 4. Al terminar el ciclo
            if (timeLeft == 0) {
                advanceToNextPhase()
            }
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
                // Progress Circle Dinámico
                Canvas(modifier = Modifier.size(280.dp)) {
                    drawArc(
                        color = Color(0xFFE0E0E0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = currentPhase.color, // Color dinámico
                        startAngle = -90f,
                        sweepAngle = (timeLeft.toFloat() / phaseTotalTime) * 360f, // Cálculo dinámico
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
                        currentPhase.title, // Título dinámico
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pagination dots (Indicador de series)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(config.totalSets) { i ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                color = if (i < currentSet) currentPhase.color else Color(0xFFD1D1D1),
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
                    colors = ButtonDefaults.buttonColors(containerColor = currentPhase.color), // Botón dinámico
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isRunning) "Pausar" else "Iniciar", fontSize = 18.sp)
                }

                Surface(
                    onClick = { timeLeft = phaseTotalTime; isRunning = false }, // Reinicia la fase actual
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar", modifier = Modifier.padding(16.dp))
                }

                Surface(
                    onClick = { advanceToNextPhase() }, // AHORA SÍ FUNCIONA EL BOTÓN
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
                Text(
                    text = if (currentPhase == SessionPhase.WORK) "💪 Mantén tu concentración en la tarea"
                    else "☕ Aprovecha para estirarte y tomar agua",
                    color = Color.Gray
                )
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
                    Icon(
                        imageVector = if (isMetronomeOn) Icons.Filled.MusicNote else Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = if (isMetronomeOn) Color(0xFF1D5DFF) else Color.Gray
                    )
                    Text(
                        text = if (isMetronomeOn) "Metrónomo Activo" else "Activar Metrónomo",
                        modifier = Modifier.padding(start = 4.dp),
                        color = if (isMetronomeOn) Color(0xFF1D5DFF) else Color.Gray,
                        fontWeight = if (isMetronomeOn) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        if (showFinishedDialog) {
            AlertDialog(
                onDismissRequest = { /* No permitir cerrar fuera para obligar a confirmar */ },
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(text = "¡Misión Cumplida!", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Has completado todas las series de:")
                        Text(
                            text = taskName.ifEmpty { "Sesión de estudio" },
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D5DFF)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("La tarea ha sido movida al historial.", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showFinishedDialog = false
                            onBack() // Regresamos a TasksScreen
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D5DFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Volver al Inicio")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}