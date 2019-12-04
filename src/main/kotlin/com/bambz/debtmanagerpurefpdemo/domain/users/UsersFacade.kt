package com.bambz.debtmanagerpurefpdemo.domain.users

import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import com.bambz.debtmanagerpurefpdemo.domain.errors.UnauthorizedError
import com.bambz.debtmanagerpurefpdemo.domain.errors.UserExistError
import com.bambz.debtmanagerpurefpdemo.domain.kernel.MonoEither
import com.bambz.debtmanagerpurefpdemo.domain.users.api.NewUserDto
import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import io.vavr.collection.HashSet
import io.vavr.collection.Set
import io.vavr.control.Either
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UsersFacade(private val userRepository: UsersRepository) {

    fun addAdmin(userFormDto: NewUserDto): MonoEither<UserDto> {
        return addUser(userFormDto, HashSet.of(Role.USER, Role.ADMIN))
    }

    fun addUser(userFormDto: NewUserDto): MonoEither<UserDto> {
        return addUser(userFormDto, HashSet.of(Role.USER))
    }

    fun login(userFormDto: NewUserDto): MonoEither<UserDto> {
        return wrapUserCredentials(userFormDto) { email, pass ->
            userRepository.findByEmail(email)
                    .map {
                        return@map if (it.equalsHashedPass(pass)) Either.right(it.toDto())
                        else UnauthorizedError.toEither<UserDto>()
                    }
                    .switchIfEmpty(UnauthorizedError.toMono<UserDto>())
        }
    }

    private fun addUser(userFormDto: NewUserDto, roles: Set<Role>): MonoEither<UserDto> {
        return wrapUserCredentials(userFormDto) { email, pass ->
            userRepository.findByEmail(email).map {
                UserExistError(email).toEither<UserDto>()
            }.switchIfEmpty(
                    createUser(email, pass, roles)
                            .let { userRepository.save(it) }
                            .map { it.toDto() }
                            .map { Either.right<AppError, UserDto>(it) }
            )
        }
    }

    private fun createUser(email: String, pass: String, roles: Set<Role>): User {
        return User(
                email = email,
                hashedPassword = BCryptPasswordEncoder().encode(pass),
                roles = roles
        )
    }

    private fun wrapUserCredentials(newUser: NewUserDto, action: (String, String) -> MonoEither<UserDto>): MonoEither<UserDto> {
        return newUser.email?.let { email ->
            newUser.password?.let { pass ->
                action(email, pass)
            }
        } ?: UnauthorizedError.toMono()
    }
}