package com.example.temporizadorapp

import android.app.Application
import com.example.temporizadorapp.data.local.AppDatabase
import com.example.temporizadorapp.data.repository.TimerRepositoryImpl

class TimerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        TimerRepositoryImpl(
            taskDao = database.taskDao(),
            templateDao = database.templateDao()
        )
    }
}