package com.bambz.debtmanagerpurefpdemo.domain.debts

import com.bambz.debtmanagerpurefpdemo.domain.debts.api.DebtResultDto
import io.vavr.collection.List
import java.math.BigDecimal
import java.time.LocalDateTime

data class Debt(private val fromUser: String, private val positions: List<DebtPosition> = List.empty()) {
    fun addPosition(amount: BigDecimal, date: LocalDateTime): Debt {
        return Debt(fromUser, positions.append(DebtPosition(amount, date)))
    }

    fun toResultDto(): DebtResultDto {
        return positions
                .map { it.value }
                .reduce { b1: BigDecimal, b2: BigDecimal -> b1.add(b2) }
                .let { result -> DebtResultDto(fromUser, result.toString()) }
    }
}