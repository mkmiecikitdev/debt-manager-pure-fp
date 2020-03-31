package com.bambz.debtmanagerpurefpdemo.domain.debts

import io.vavr.collection.HashMap
import io.vavr.collection.List
import io.vavr.collection.Map

data class DebtsMap(private val map: Map<String, List<DebtValue>>) {

    fun add(debtorId: String, debtValue: DebtValue): DebtsMap {
        val list = map[debtorId].getOrElse { List.empty() }
        return DebtsMap(map.put(debtorId, list.push(debtValue)))
    }

    fun toDbObject(): kotlin.collections.Map<String, kotlin.collections.List<DebtValue>> = map
            .mapValues { it.toJavaList() }
            .toJavaMap()

    fun toDbUserSet(): kotlin.collections.Set<String> = map.keySet().toJavaSet()

    companion object {

        fun fromDbObject(map: kotlin.collections.Map<String, kotlin.collections.List<DebtValue>>): DebtsMap {
            return DebtsMap(HashMap
                    .ofAll(map)
                    .mapValues { List.ofAll(it) })
        }

    }

}