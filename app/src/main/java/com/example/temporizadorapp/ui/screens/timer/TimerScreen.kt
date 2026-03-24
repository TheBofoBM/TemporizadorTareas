package com.example.temporizadorapp.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temporizadorapp.ui.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onStartSession: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val config by viewModel.currentConfig.collectAsState()
    val taskName by viewModel.taskName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FF))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Icono superior
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF1D5DFF)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Temporizador Pomodoro",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )
        Text(
            text = "Configura tu sesión de estudio",
            fontSize = 16.sp,
            color = Color(0xFF5D5F62)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de navegación rápida
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavButton(
                icon = Icons.Outlined.Book,
                label = "Plantillas",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToTemplates
            )
            NavButton(
                icon = Icons.AutoMirrored.Outlined.List,
                label = "Tareas",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToTasks
            )
            NavButton(
                icon = Icons.Outlined.History,
                label = "Historial",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToHistory
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ConfigLabel(icon = Icons.Outlined.TrackChanges, label = "Nombre de la tarea")
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { viewModel.updateTaskName(it) },
                    placeholder = { Text("ej. Tarea de Matemáticas") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color(0xFFF8F9FA),
                        unfocusedContainerColor = Color(0xFFF8F9FA)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigLabel(icon = Icons.Outlined.Timer, label = "Trabajo (min)")
                        ConfigInput(value = config.workTime.toString()) {
                            viewModel.updateConfig(config.copy(workTime = it.toIntOrNull() ?: 0))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigLabel(icon = Icons.Outlined.Coffee, label = "Descanso (min)")
                        ConfigInput(value = config.breakTime.toString()) {
                            viewModel.updateConfig(config.copy(breakTime = it.toIntOrNull() ?: 0))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigLabel(icon = Icons.Outlined.Timer, label = "Descanso Largo (min)")
                        ConfigInput(value = config.longBreakTime.toString()) {
                            viewModel.updateConfig(config.copy(longBreakTime = it.toIntOrNull() ?: 0))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ConfigLabel(icon = Icons.Outlined.Refresh, label = "Total de Series")
                        ConfigInput(value = config.totalSets.toString()) {
                            viewModel.updateConfig(config.copy(totalSets = it.toIntOrNull() ?: 0))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(16.dp))

                SwitchRow(
                    label = "Inicio automático",
                    checked = config.autoStart,
                    onCheckedChange = { viewModel.updateConfig(config.copy(autoStart = it)) }
                )
                SwitchRow(
                    label = "Mantener pantalla encendida",
                    checked = config.keepScreenOn,
                    onCheckedChange = { viewModel.updateConfig(config.copy(keepScreenOn = it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onStartSession,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D5DFF)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Comenzar Sesión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NavButton(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1A1C1E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(text = label, fontSize = 12.sp)
        }
    }
}

@Composable
fun ConfigLabel(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color(0xFF1A1C1E)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E))
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ConfigInput(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color(0xFFF8F9FA),
            unfocusedContainerColor = Color(0xFFF8F9FA)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, color = Color(0xFF1A1C1E))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1D5DFF)
            )
        )
    }
}
