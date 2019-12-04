package com.bambz.debtmanagerpurefpdemo.domain.debts

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryDebtsRepository(private var debtsMap: AtomicReference<Map<String, Debts>> = AtomicReference(HashMap.empty())) : DebtsRepository {

    override fun findByUser(userId: String): Mono<Debts> {
        return debtsMap.get()[userId]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun save(debts: Debts): Mono<Debts> {
        debtsMap.updateAndGet { it.put(debts.userId, debts) }
        return Mono.just(debts)
    }

}