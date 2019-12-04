package com.bambz.debtmanagerpurefpdemo.domain.debts

import reactor.core.publisher.Mono

interface DebtsRepository {

    fun findByUser(userId: String): Mono<Debts>

    fun save(debts: Debts): Mono<Debts>

}