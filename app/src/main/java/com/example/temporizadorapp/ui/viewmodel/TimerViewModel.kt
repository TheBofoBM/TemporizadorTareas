package com.example.temporizadorapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.temporizadorapp.domain.model.SessionConfig
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import com.example.temporizadorapp.domain.repository.TimerRepository
import com.example.temporizadorapp.util.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(
    application: Application,
    private val repository: TimerRepository
) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler(application)

    // --- 1. MEMORIA DE LA BASE DE DATOS ---
    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val templates: StateFlow<List<Template>> = repository.getAllTemplates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- 2. ESTADO TEMPORAL (Para la pantalla del cronómetro) ---
    private val _currentConfig = MutableStateFlow(SessionConfig())
    val currentConfig: StateFlow<SessionConfig> = _currentConfig.asStateFlow()

    private val _taskName = MutableStateFlow("")
    val taskName: StateFlow<String> = _taskName.asStateFlow()

    private val _currentTaskId = MutableStateFlow<String?>(null)
    val currentTaskId: StateFlow<String?> = _currentTaskId.asStateFlow()

    fun deleteTemplate(template: Template) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
        }
    }

    fun updateTemplate(template: Template) {
        viewModelScope.launch {
            repository.updateTemplate(template)
        }
    }

    fun useTemplate(template: Template) {
        updateConfig(template.config)
        updateTaskName(template.name)
    }

    // --- 3. FUNCIONES DE ACTUALIZACIÓN ---
    fun updateTaskName(name: String) {
        _taskName.value = name
    }

    fun updateConfig(config: SessionConfig) {
        _currentConfig.value = config
    }

    fun addTask(task: Task) {
        viewModelScope.launch { 
            repository.saveTask(task)
            // Programar alarma para la tarea
            alarmScheduler.schedule(task)
        }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch { 
            repository.updateTaskStatus(taskId, isCompleted)
        }
    }

    fun addTemplate(template: Template) {
        viewModelScope.launch { repository.saveTemplate(template) }
    }

    fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, isCompleted)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            alarmScheduler.cancel(task)
        }
    }

    fun setCurrentTask(task: Task) {
        _taskName.value = task.name
        _currentTaskId.value = task.id
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val today = java.time.LocalDate.now().toString()
            // Asumiendo que actualizamos el repo para aceptar la fecha
            // repository.updateTaskStatus(taskId, true, today)
            repository.updateTaskStatus(taskId, true)
        }
    }

    // --- 4. CONFIGURACIÓN DEL VIEWMODEL ---
    class Factory(
        private val application: Application,
        private val repository: TimerRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TimerViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun completeCurrentTask() {
        viewModelScope.launch {
            val currentTasks = tasks.value
            val taskToComplete = currentTasks.find { it.name == _taskName.value && !it.isCompleted }

            taskToComplete?.let {
                repository.updateTaskStatus(it.id, true)
            }
        }
    }
}