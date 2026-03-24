package com.example.temporizadorapp.domain.model

import java.util.UUID

data class Template(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val config: SessionConfig = SessionConfig()
)
