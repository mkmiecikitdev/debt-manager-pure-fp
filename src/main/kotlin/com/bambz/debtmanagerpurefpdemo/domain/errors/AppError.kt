package com.bambz.debtmanagerpurefpdemo.domain.errors

import io.vavr.control.Either
import reactor.core.publisher.Mono
import java.math.BigDecimal

sealed class AppError(open val message: String = "") {
    fun <T> toEither(): Either<AppError, T> = Either.left<AppError, T>(this)
    fun <T> toMono() = Mono.just(toEither<T>())
}

data class UserExistError(val email: String): AppError()
object UnauthorizedError: AppError()
data class IncorrectAmountError(val amount: BigDecimal, override val message: String = "Incorrect amount"): AppError()
object BadFormRequestError: AppError()



