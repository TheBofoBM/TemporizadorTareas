package com.example.temporizadorapp.domain.repository

import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean)

    fun getAllTemplates(): Flow<List<Template>>
    suspend fun getTemplateById(templateId: String): Template?
    suspend fun saveTemplate(template: Template)
    suspend fun deleteTemplate(template: Template)

    suspend fun updateTemplate(template: Template)
}