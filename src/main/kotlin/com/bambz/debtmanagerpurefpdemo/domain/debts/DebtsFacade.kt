package com.bambz.debtmanagerpurefpdemo.domain.debts

import com.bambz.debtmanagerpurefpdemo.domain.debts.api.NewDebtValueForm
import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import com.bambz.debtmanagerpurefpdemo.domain.errors.BadFormRequestError
import com.bambz.debtmanagerpurefpdemo.domain.kernel.Attempt
import com.bambz.debtmanagerpurefpdemo.domain.kernel.MonoEither
import com.bambz.debtmanagerpurefpdemo.domain.kernel.TimeService
import com.bambz.debtmanagerpurefpdemo.domain.kernel.merge
import io.vavr.control.Either
import io.vavr.control.Try
import java.math.BigDecimal

class DebtsFacade(private val debtsRepository: DebtsRepository, private val timeService: TimeService) {

    fun addDebt(ctxId: String, form: NewDebtValueForm): MonoEither<Debtor> {
        return form.debtorId?.let { debtorId ->
            mapStringToMonoEither(debtorId, ctxId, form)
        } ?: BadFormRequestError.toMono()
    }

    private fun mapStringToMonoEither(debtorId: String, ctxId: String, form: NewDebtValueForm): MonoEither<Debtor> {
        return debtsRepository.findById(debtorId).flatMap {
            mapDebtorToAttempt(it, ctxId, form)
        }
    }

    private fun mapDebtorToAttempt(debtor: Debtor, ctxId: String, form: NewDebtValueForm): MonoEither<Debtor> {
        return tryCreateValidNewDebtValue(ctxId, form).map { newDebtValue ->
            debtsRepository.save(debtor.add(newDebtValue))
                    .map { Either.right<AppError, Debtor>(it) }
        }
                .mapLeft { it.toMono<Debtor>() }
                .merge()
    }

    private fun tryCreateValidNewDebtValue(ctxId: String, form: NewDebtValueForm): Attempt<NewDebtValue> {
        return form.amount?.let { amount ->
            Try.of {
                NewDebtValue(debtorId = ctxId, now = timeService.now(), value = BigDecimal(amount))
            }
                    .toEither<AppError>(BadFormRequestError)
        } ?: BadFormRequestError.toEither()
    }

}