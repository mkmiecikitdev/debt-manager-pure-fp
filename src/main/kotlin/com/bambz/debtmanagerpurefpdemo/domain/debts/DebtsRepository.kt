package com.bambz.debtmanagerpurefpdemo.domain.debts

import reactor.core.publisher.Mono

interface DebtsRepository {

    fun findById(id: String): Mono<Debtor>

    fun save(debtor: Debtor): Mono<Debtor>

}