package com.example.temporizadorapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val scheduledTime: String,
    val scheduledDays: String,
    val templateId: Int?,
    val isCompleted: Boolean = false
)