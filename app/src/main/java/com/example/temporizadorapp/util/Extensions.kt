package com.example.temporizadorapp.util

import com.example.temporizadorapp.data.local.entities.TaskEntity
import com.example.temporizadorapp.data.local.entities.TemplateEntity
import com.example.temporizadorapp.domain.model.SessionConfig
import com.example.temporizadorapp.domain.model.Task
import com.example.temporizadorapp.domain.model.Template
import java.time.LocalTime

fun TaskEntity.toDomain(): Task {
    return Task(
        id = this.id.toString(),
        name = this.title,
        startTime = try { LocalTime.parse(this.scheduledTime) } catch (e: Exception) { LocalTime.now() },
        scheduledDays = if (this.scheduledDays.isEmpty()) emptySet() else this.scheduledDays.split(",").map { it.toInt() }.toSet(),
        templateId = this.templateId?.toString() ?: "",
        isCompleted = this.isCompleted
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = this.id.toIntOrNull() ?: 0,
        title = this.name,
        scheduledTime = this.startTime.toString(),
        scheduledDays = this.scheduledDays.joinToString(","),
        templateId = this.templateId.toIntOrNull(),
        isCompleted = this.isCompleted
    )
}


// --- CONVERSORES DE PLANTILLAS ---

fun TemplateEntity.toDomain(): Template {
    return Template(
        id = this.id.toString(),
        name = this.name,
        config = SessionConfig(
            workTime = this.workMinutes,       // Corregido aquí
            breakTime = this.shortBreakMinutes, // Corregido aquí
            longBreakTime = this.longBreakMinutes, // Corregido aquí
            totalSets = this.totalSeries       // Corregido aquí
        )
    )
}

fun Template.toEntity(): TemplateEntity {
    return TemplateEntity(
        id = this.id.toIntOrNull() ?: 0,
        name = this.name,
        workMinutes = this.config.workTime,       // Corregido aquí
        shortBreakMinutes = this.config.breakTime, // Corregido aquí
        longBreakMinutes = this.config.longBreakTime, // Corregido aquí
        totalSeries = this.config.totalSets       // Corregido aquí
    )
}