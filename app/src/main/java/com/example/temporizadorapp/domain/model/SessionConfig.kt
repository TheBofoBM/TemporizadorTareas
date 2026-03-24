package com.example.temporizadorapp.domain.model

data class SessionConfig(
    val workTime: Int = 25,
    val breakTime: Int = 5,
    val longBreakTime: Int = 15,
    val totalSets: Int = 4,
    val autoStart: Boolean = false,
    val keepScreenOn: Boolean = false
)
