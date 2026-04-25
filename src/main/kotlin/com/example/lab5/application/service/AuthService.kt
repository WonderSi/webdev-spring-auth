package com.example.lab5.application.service

import com.example.lab5.domain.exception.AlreadyExistsException
import com.example.lab5.domain.exception.NotFoundException
import com.example.lab5.domain.model.Role
import com.example.lab5.infrastructure.jpa.entity.UserEntity
import com.example.lab5.infrastructure.jpa.repository.UserJpaRepository
import com.example.lab5.infrastructure.security.JwtService
import com.example.lab5.web.dto.AuthResponse
import com.example.lab5.web.dto.LoginRequest
import com.example.lab5.web.dto.RegisterRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserJpaRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw AlreadyExistsException("Пользователь с email ${request.email} уже существует")
        }

        val user = UserEntity(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            role = Role.USER
        )

        val saved = userRepository.save(user)
        val token = jwtService.generateToken(saved.email, saved.role.name)
        return AuthResponse(token, saved.email, saved.role.name)
    }

    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = userRepository.findByEmail(request.email)
            ?: throw NotFoundException("Пользователь не найден")

        val token = jwtService.generateToken(user.email, user.role.name)
        return AuthResponse(token, user.email, user.role.name)
    }
}
