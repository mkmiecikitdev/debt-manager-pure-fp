package com.bambz.debtmanagerpurefpdemo.domain.debts

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryDebtsRepository(private var debtsMap: AtomicReference<Map<String, Debtor>> = AtomicReference(HashMap.empty())) : DebtsRepository {

    override fun findById(id: String): Mono<Debtor> {
        return debtsMap.get()[id]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun save(debtor: Debtor): Mono<Debtor> {
        debtsMap.updateAndGet { it.put(debtor.id, debtor) }
        return Mono.just(debtor)
    }

}