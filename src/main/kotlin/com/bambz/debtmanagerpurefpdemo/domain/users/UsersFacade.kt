package com.bambz.debtmanagerpurefpdemo.domain.users

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade
import com.bambz.debtmanagerpurefpdemo.domain.errors.*
import com.bambz.debtmanagerpurefpdemo.domain.kernel.MonoEither
import com.bambz.debtmanagerpurefpdemo.domain.users.api.LoginUserDto
import com.bambz.debtmanagerpurefpdemo.domain.users.api.NewUserDto
import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import io.vavr.collection.HashSet
import io.vavr.collection.Set
import io.vavr.control.Either
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UsersFacade(private val userRepository: UsersRepository, private val debtsFacade: DebtsFacade) {

    fun addAdmin(userFormDto: NewUserDto): MonoEither<UserDto> {
        return addUser(userFormDto, HashSet.of(Role.USER, Role.ADMIN))
    }

    fun addUser(userFormDto: NewUserDto): MonoEither<UserDto> {
        return addUser(userFormDto, HashSet.of(Role.USER)).map {
            it.onEach { userDto ->
                debtsFacade.addDebtor(userDto.email)
            }
        }
    }

    fun login(loginUserDto: LoginUserDto): MonoEither<UserDto> {
        return checkUserLoginForm(loginUserDto) { email, pass ->
            userRepository.findByEmail(email)
                    .map {
                        return@map if (it.equalsHashedPass(pass)) Either.right(it.toDto())
                        else UnauthorizedError.toEither<UserDto>()
                    }
                    .switchIfEmpty(UnauthorizedError.toMono())
        }
    }

    private fun addUser(userFormDto: NewUserDto, roles: Set<Role>): MonoEither<UserDto> {
        return checkUserRegisterForm(userFormDto) { email, pass ->
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

    private fun checkUserLoginForm(loginUser: LoginUserDto, action: (String, String) -> MonoEither<UserDto>): MonoEither<UserDto> {
        return loginUser.email?.let { email ->
            loginUser.password?.let { pass ->
                action(email, pass)
            }
        } ?: UnauthorizedError.toMono()
    }

    private fun checkUserRegisterForm(newUser: NewUserDto, action: (String, String) -> MonoEither<UserDto>): MonoEither<UserDto> {
        return newUser.email?.let { email ->
            newUser.password?.let { pass ->
                newUser.confirmPassword?.let { confirmPass ->
                    if (confirmPass != pass) {
                        DifferentPasswordsError
                    }
                    action(email, pass)
                }
            }
        } ?: BadFormRequestError.toMono()
    }
}