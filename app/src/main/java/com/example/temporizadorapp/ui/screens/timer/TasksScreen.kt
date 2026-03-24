package com.example.temporizadorapp.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.ui.viewmodel.TimerViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TimerViewModel, onBack: () -> Unit) {
    val tasks by viewModel.tasks.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Todas", "Diarias", "Terminadas")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mis Tareas", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("Gestiona tu trabajo y sesiones de estudio", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Row(modifier = Modifier.padding(end = 8.dp)) {
                        TextButton(onClick = { /* Navigate to templates */ }) {
                            Icon(Icons.Outlined.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Plantillas", color = Color.Black)
                        }
                        TextButton(onClick = { /* Navigate to history */ }) {
                            Icon(Icons.Outlined.History, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Historial", color = Color.Black)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF9E6))
            )
        },
        containerColor = Color(0xFFFFF9E6)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mis Actividades", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Button(
                            onClick = { /* New Task */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Nueva Tarea")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusSummaryCard("Pendientes", tasks.count { !it.isCompleted }, Color(0xFFE3F2FD), Color(0xFF1D5DFF), Modifier.weight(1f))
                        StatusSummaryCard("En Progreso", 0, Color(0xFFFFF9C4), Color(0xFFFBC02D), Modifier.weight(1f))
                        StatusSummaryCard("Completadas", tasks.count { it.isCompleted }, Color(0xFFE8F5E9), Color(0xFF4CAF50), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = Color.Black,
                        edgePadding = 0.dp,
                        indicator = {},
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Surface(
                                        color = if (selectedTab == index) Color.White else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(title, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (tasks.isEmpty()) {
                        EmptyTasksView()
                    } else {
                        val filteredTasks = when (selectedTab) {
                            1 -> tasks.filter { /* Logic for daily */ true }
                            2 -> tasks.filter { it.isCompleted }
                            else -> tasks
                        }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredTasks) { task ->
                                TaskItem(task)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusSummaryCard(label: String, count: Int, bgColor: Color, textColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun EmptyTasksView() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Timer,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFFFCCBC)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No hay tareas registradas", fontWeight = FontWeight.Bold)
        Text("Crea tu primera tarea para comenzar a estudiar", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun TaskItem(task: Task) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = {})
            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, fontWeight = FontWeight.SemiBold)
                Text("${task.startTime.format(timeFormatter)} - ${task.scheduledDays.joinToString(", ")}", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar", tint = Color(0xFF1D5DFF))
            }
        }
    }
}
