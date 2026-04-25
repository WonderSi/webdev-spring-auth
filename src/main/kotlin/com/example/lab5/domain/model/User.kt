package com.example.lab5.domain.model

data class User(
    val id: Long = 0,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String = "",
    val isActive: Boolean = true,
    val role: Role = Role.USER
)