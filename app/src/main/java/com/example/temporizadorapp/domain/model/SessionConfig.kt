package com.example.temporizadorapp.domain.model

data class SessionConfig(
    val workTime: Int,
    val breakTime: Int,
    val totalSets: Int,
    val autoStart: Boolean
)
