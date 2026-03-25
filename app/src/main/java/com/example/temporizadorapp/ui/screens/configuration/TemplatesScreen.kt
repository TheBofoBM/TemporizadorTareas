package com.example.temporizadorapp.ui.screens.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.window.Dialog
import com.example.temporizadorapp.domain.model.SessionConfig
import com.example.temporizadorapp.domain.model.Template
import com.example.temporizadorapp.ui.viewmodel.TimerViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(viewModel: TimerViewModel, onBack: () -> Unit) {
    val templates by viewModel.templates.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Estado para saber qué plantilla estamos editando
    var templateToEdit by remember { mutableStateOf<Template?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plantillas de Sesiones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nueva")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFDF7FF))
            )
        },
        containerColor = Color(0xFFFDF7FF)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Gestiona tus configuraciones predefinidas", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(templates) { template ->
                    TemplateItem(
                        template = template,
                        onUse = {
                            viewModel.updateConfig(template.config)
                            viewModel.updateTaskName(template.name)
                            onBack()
                        },
                        onEdit = { templateToEdit = template },
                        onDelete = { viewModel.deleteTemplate(template) } // Asegúrate de tener esta función en tu VM
                    )
                }
            }
        }
    }

    // Diálogo para Crear
    if (showAddDialog) {
        AddTemplateDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, config ->
                viewModel.addTemplate(Template(name = name, description = desc, config = config))
                showAddDialog = false
            }
        )
    }

    // Diálogo para Editar
    templateToEdit?.let { template ->
        AddTemplateDialog(
            initialTemplate = template,
            onDismiss = { templateToEdit = null },
            onConfirm = { name, desc, config ->
                // Actualizamos la plantilla existente manteniendo su ID
                viewModel.addTemplate(template.copy(name = name, description = desc, config = config))
                templateToEdit = null
            }
        )
    }
}

@Composable
fun TemplateItem(
    template: Template,
    onUse: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(template.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Eliminar", tint = Color(0xFFE53935))
                    }
                }
            }
            
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TemplateTag(Icons.Outlined.Timer, "${template.config.workTime} min trabajo")
                TemplateTag(Icons.Outlined.Coffee, "${template.config.breakTime} min descanso")
                TemplateTag(Icons.Outlined.Refresh, "${template.config.totalSets} series")
            }

            Button(
                onClick = onUse,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Usar esta Plantilla")
            }
        }
    }
}

@Composable
fun TemplateTag(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF5F5F5),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF1D5DFF))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTemplateDialog(
    initialTemplate: Template? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, SessionConfig) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var workTime by remember { mutableStateOf("25") }
    var breakTime by remember { mutableStateOf("5") }
    var longBreakTime by remember { mutableStateOf("15") }
    var totalSets by remember { mutableStateOf("4") }
    var autoStart by remember { mutableStateOf(false) }
    var keepScreenOn by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nueva Plantilla", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Nombre de la Plantilla *", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("ej. Sesión Intensiva") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Descripción", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = desc, onValueChange = { desc = it }, placeholder = { Text("ej. Para materias difíciles...") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Trabajo (min)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = workTime, onValueChange = { workTime = it }, modifier = Modifier.fillMaxWidth())
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Descanso (min)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = breakTime, onValueChange = { breakTime = it }, modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Descanso Largo (min)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = longBreakTime, onValueChange = { longBreakTime = it }, modifier = Modifier.fillMaxWidth())
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total de Series", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = totalSets, onValueChange = { totalSets = it }, modifier = Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Inicio automático")
                    Switch(checked = autoStart, onCheckedChange = { autoStart = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Mantener pantalla encendida")
                    Switch(checked = keepScreenOn, onCheckedChange = { keepScreenOn = it })
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = { 
                            onConfirm(name, desc, SessionConfig(workTime.toIntOrNull() ?: 25, breakTime.toIntOrNull() ?: 5, longBreakTime.toIntOrNull() ?: 15, totalSets.toIntOrNull() ?: 4, autoStart, keepScreenOn))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
                    ) { Text(if (initialTemplate == null) "Crear" else "Guardar Cambios") }
                }
            }
        }
    }
}

