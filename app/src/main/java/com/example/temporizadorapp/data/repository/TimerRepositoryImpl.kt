package com.example.temporizadorapp.data.repository

import com.example.temporizadorapp.data.local.dao.TaskDao
import com.example.temporizadorapp.data.local.dao.TemplateDao
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import com.example.temporizadorapp.domain.repository.TimerRepository
import com.example.temporizadorapp.util.toDomain
import com.example.temporizadorapp.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerRepositoryImpl(
    private val taskDao: TaskDao,
    private val templateDao: TemplateDao
) : TimerRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean) {
        val id = taskId.toIntOrNull()
        if (id != null) {
            taskDao.updateTaskStatus(id, isCompleted)
        }
    }

    override fun getAllTemplates(): Flow<List<Template>> {
        return templateDao.getAllTemplates().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTemplateById(templateId: String): Template? {
        val id = templateId.toIntOrNull() ?: return null
        return templateDao.getTemplateById(id)?.toDomain()
    }

    override suspend fun saveTemplate(template: Template) {
        templateDao.insertTemplate(template.toEntity())
    }

    override suspend fun deleteTemplate(template: Template) {
        templateDao.deleteTemplate(template.toEntity())
    }

    override suspend fun updateTemplate(template: Template) {
        templateDao.updateTemplate(template.toEntity()) // Convierte a Entity si es necesario
    }
}