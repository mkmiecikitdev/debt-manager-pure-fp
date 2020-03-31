package com.bambz.debtmanagerpurefpdemo.domain.debts

import io.vavr.collection.Map

data class DebtsMap(private val map: Map<String, DebtValue>) {

    fun add(debtorId: String, debtValue: DebtValue): DebtsMap {
        return DebtsMap(map.put(debtorId, debtValue))
    }

}