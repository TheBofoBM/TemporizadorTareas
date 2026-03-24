package com.example.temporizadorapp.domain.model

import java.time.LocalTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startTime: LocalTime,
    val scheduledDays: Set<Int>, // 1 = Monday, ..., 7 = Sunday
    val templateId: String,
    val isCompleted: Boolean = false,
    val lastRunDate: String? = null // ISO date string
)
