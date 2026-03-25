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
fun TasksScreen(
    viewModel: TimerViewModel,
    onBack: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Todas", "Diarias", "Terminadas")

    // ESTADO PARA EL DIÁLOGO
    var showAddDialog by remember { mutableStateOf(false) }

    // Mostrar el diálogo si el estado es true
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                val newTask = Task(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    startTime = java.time.LocalTime.now(),
                    // Usamos el día actual (1-7) para que aparezca hoy mismo
                    scheduledDays = setOf(java.time.LocalDate.now().dayOfWeek.value),
                    templateId = "manual", // Valor obligatorio para el constructor
                    isCompleted = false,
                    lastRunDate = null
                )
                viewModel.addTask(newTask)
            }
        )
    }

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
                        TextButton(onClick = onNavigateToTemplates) {
                            Icon(Icons.Outlined.Book, contentDescription = null)
                            Text("Plantillas")
                        }
                        TextButton(onClick = onNavigateToHistory) {
                            Icon(Icons.Outlined.History, contentDescription = null)
                            Text("Historial")
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
                            onClick = { showAddDialog = true },
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
                        // FILTRADO DINÁMICO
                        val filteredTasks = when (selectedTab) {
                            1 -> { // Pestaña "Diarias"
                                val today = java.time.LocalDate.now().dayOfWeek.value
                                tasks.filter { it.scheduledDays.contains(today) && !it.isCompleted }
                            }
                            2 -> tasks.filter { it.isCompleted } // Pestaña "Terminadas"
                            else -> tasks // Pestaña "Todas"
                        }

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filteredTasks) { task ->
                                TaskItem(
                                    task = task,
                                    onToggle = { isChecked ->
                                        viewModel.updateTaskStatus(task.id, isChecked)
                                    },
                                    onPlay = {
                                        viewModel.setCurrentTask(task)
                                        onBack() // Volver al timer
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var taskName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea") },
        text = {
            TextField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = { Text("Ej: Estudiar Kotlin") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(taskName); onDismiss() }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
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
fun TaskItem(task: Task, onToggle: (Boolean) -> Unit, onPlay: () -> Unit) {
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
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle(it) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontWeight = FontWeight.SemiBold,
                    style = if (task.isCompleted) LocalTextStyle.current.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                        color = Color.Gray
                    ) else LocalTextStyle.current
                )
                val daysMap = mapOf(1 to "Lun", 2 to "Mar", 3 to "Mié", 4 to "Jue", 5 to "Vie", 6 to "Sáb", 7 to "Dom")
                val daysString = task.scheduledDays.map { daysMap[it] }.joinToString(", ")

                // Luego en el Text de TaskItem:
                Text("${task.startTime.format(timeFormatter)} - $daysString", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onPlay) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Iniciar", tint = Color(0xFF1D5DFF))
            }
        }
    }
}