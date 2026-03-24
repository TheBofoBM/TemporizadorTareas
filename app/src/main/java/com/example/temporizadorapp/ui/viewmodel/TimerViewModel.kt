package com.example.temporizadorapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.temporizadorapp.domain.model.SessionConfig
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import java.util.UUID

class TimerViewModel : ViewModel() {
    private val _templates = MutableStateFlow<List<Template>>(
        listOf(
            Template(name = "Estudio Pomodoro", config = SessionConfig(25, 5, 15, 4)),
            Template(name = "Ejercicio", config = SessionConfig(40, 10, 20, 3))
        )
    )
    val templates: StateFlow<List<Template>> = _templates.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _currentConfig = MutableStateFlow(SessionConfig())
    val currentConfig: StateFlow<SessionConfig> = _currentConfig.asStateFlow()

    private val _taskName = MutableStateFlow("")
    val taskName: StateFlow<String> = _taskName.asStateFlow()

    fun updateTaskName(name: String) {
        _taskName.value = name
    }

    fun updateConfig(config: SessionConfig) {
        _currentConfig.value = config
    }

    fun addTemplate(template: Template) {
        _templates.value += template
    }

    fun addTask(task: Task) {
        _tasks.value += task
    }
}
