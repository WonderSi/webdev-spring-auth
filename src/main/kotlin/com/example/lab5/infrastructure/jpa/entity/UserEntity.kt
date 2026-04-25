package com.example.lab5.infrastructure.jpa.entity

import com.example.lab5.domain.model.Role
import com.example.lab5.domain.model.User
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    private val password: String,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER
) : UserDetails {
    constructor() : this(0, "", "", "", "", true, Role.USER)

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    override fun getPassword(): String = password
    override fun getUsername(): String = email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun toDomain() = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        password = password,
        isActive = isActive,
        role = role
    )

    companion object {
        fun fromDomain(user: User) = UserEntity(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            password = user.password,
            isActive = user.isActive,
            role = user.role
        )
    }
}