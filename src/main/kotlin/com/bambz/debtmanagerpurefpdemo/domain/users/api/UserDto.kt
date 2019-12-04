package com.bambz.debtmanagerpurefpdemo.domain.users.api

import com.bambz.debtmanagerpurefpdemo.domain.users.Role
import io.vavr.collection.Set

data class UserDto(
        val email: String,
        val roles: Set<Role>
)