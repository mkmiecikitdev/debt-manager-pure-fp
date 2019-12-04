package com.bambz.debtmanagerpurefpdemo.domain.debts

import com.bambz.debtmanagerpurefpdemo.domain.debts.api.DebtsDto
import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import com.bambz.debtmanagerpurefpdemo.domain.errors.IncorrectAmountError
import io.vavr.collection.List
import io.vavr.collection.Map
import io.vavr.control.Either
import java.math.BigDecimal
import java.time.LocalDateTime

data class Debts(
        val userId: String,
        val debtsMap: Map<String, Debt>
) {

    fun add(fromUser: String, amount: BigDecimal, date: LocalDateTime): Either<AppError, Debts> {
        if (amount == BigDecimal.ZERO) {
            return IncorrectAmountError(amount).toEither()
        }

        return addNewDebtPosition(fromUser, amount, date)
                .let { Either.right<AppError, Debts>(it) }
    }

    fun minus(fromUser: String, amount: BigDecimal, date: LocalDateTime): Either<AppError, Debts> {
        if (amount == BigDecimal.ZERO) {
            return IncorrectAmountError(amount).toEither()
        }

        return addNewDebtPosition(fromUser, amount, date)
                .let { Either.right<AppError, Debts>(it) }
    }

    fun clear(fromUser: String): Debts {
        return Debts(userId, debtsMap.put(fromUser, Debt(fromUser)))
    }

    fun toDto(): DebtsDto {
        return debtsMap.values()
                .map { it.toResultDto() }
                .toList()
                .let { DebtsDto(userId, it) }
    }

    private fun addNewDebtPosition(otherUserId: String, amount: BigDecimal, date: LocalDateTime): Debts {
        return debtsMap[otherUserId]
                .map { it.addPosition(amount, date) }
                .map { debtsMap.put(otherUserId, it) }
                .map { Debts(userId, it) }
                .getOrElse { createFirstDebts(otherUserId, amount, date) }
    }

    private fun createFirstDebts(otherUserId: String, amount: BigDecimal, date: LocalDateTime): Debts {
        return List.of(DebtPosition(amount, date))
                .let { Debt(otherUserId, it) }
                .let { Debts(userId, debtsMap.put(otherUserId, it)) }
    }
}