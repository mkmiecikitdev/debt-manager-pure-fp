package com.bambz.debtmanagerpurefpdemo.domain.debts

import com.bambz.debtmanagerpurefpdemo.domain.debts.api.AddDebtDto
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.ClearDebtDto
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.DebtsDto
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.MinusDebtDto
import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import com.bambz.debtmanagerpurefpdemo.domain.errors.BadFormRequestError
import com.bambz.debtmanagerpurefpdemo.domain.kernel.MonoEither
import com.bambz.debtmanagerpurefpdemo.domain.kernel.TimeService
import com.bambz.debtmanagerpurefpdemo.domain.kernel.merge
import io.vavr.collection.HashMap
import io.vavr.control.Either
import reactor.core.publisher.Mono
import java.math.BigDecimal

class DebtsFacade(private val debtsRepository: DebtsRepository, private val timeService: TimeService) {

    fun addDebt(contextUserId: String, addDebtDto: AddDebtDto): MonoEither<DebtsDto> {
        if (addDebtDto.amount == null || addDebtDto.userId == null) {
            return BadFormRequestError.toMono()
        }

        return getDebtsOrNew(addDebtDto.userId).flatMap { debts ->
            debts.add(contextUserId, BigDecimal(addDebtDto.amount), timeService.now())
                    .map { updatedDebts -> saveToEither(updatedDebts) }
                    .mapLeft { it.toMono<DebtsDto>() }
                    .merge()
        }
    }

    fun minusDebt(contextUserId: String, minusDebtDto: MinusDebtDto): MonoEither<DebtsDto> {
        if (minusDebtDto.amount == null || minusDebtDto.userId == null) {
            return BadFormRequestError.toMono()
        }

        return getDebtsOrNew(minusDebtDto.userId).flatMap { debts ->
            debts.minus(contextUserId, BigDecimal(minusDebtDto.amount), timeService.now())
                    .map { updatedDebts -> saveToEither(updatedDebts) }
                    .mapLeft { it.toMono<DebtsDto>() }
                    .merge()
        }
    }

    fun clearDebt(clearDebtDto: ClearDebtDto): MonoEither<DebtsDto> {
        if (clearDebtDto.userId == null || clearDebtDto.fromUser == null) {
            return BadFormRequestError.toMono()
        }

        return getDebtsOrNew(clearDebtDto.userId).flatMap { debts ->
            debts.clear(clearDebtDto.fromUser)
                    .let { updatedDebts -> saveToEither(updatedDebts) }
        }
    }

    private fun getDebtsOrNew(userId: String): Mono<Debts> {
        return debtsRepository.findByUser(userId)
                .defaultIfEmpty(newDebts(userId))
    }

    private fun newDebts(userId: String): Debts {
        return Debts(userId, HashMap.empty())
    }

    private fun saveToEither(debts: Debts): MonoEither<DebtsDto> {
        return debtsRepository.save(debts)
                .map {
                    Either.right<AppError, DebtsDto>(it.toDto())
                }
    }

    private fun save(debts: Debts): Mono<DebtsDto> {
        return debtsRepository.save(debts)
                .map { it.toDto() }
    }


}