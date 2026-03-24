package com.example.temporizadorapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.temporizadorapp.domain.model.SessionConfig
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import com.example.temporizadorapp.domain.repository.TimerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimerViewModel(private val repository: TimerRepository) : ViewModel() {

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


    // --- 3. FUNCIONES DE ACTUALIZACIÓN ---
    fun updateTaskName(name: String) {
        _taskName.value = name
    }

    fun updateConfig(config: SessionConfig) {
        _currentConfig.value = config
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repository.saveTask(task) }
    }

    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch { repository.updateTaskStatus(taskId, isCompleted) }
    }

    fun addTemplate(template: Template) {
        viewModelScope.launch { repository.saveTemplate(template) }
    }

    // --- 4. CONFIGURACIÓN DEL VIEWMODEL ---
    class Factory(private val repository: TimerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TimerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}