package com.bambz.debtmanagerpurefpdemo.domain.users

import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import io.vavr.collection.Set
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class User(
        val email: String,
        private val hashedPassword: String,
        private val roles: Set<Role>
) {

    fun equalsHashedPass(rawPassword: String): Boolean {
        return BCryptPasswordEncoder().matches(rawPassword, hashedPassword)
    }

    fun toDto(): UserDto {
        return UserDto(
                email = email,
                roles = roles
        )
    }
}