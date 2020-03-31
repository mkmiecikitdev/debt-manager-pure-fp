package com.bambz.debtmanagerpurefpdemo.domain.debts

class Debtor (val id: String, private val debtsMap: DebtsMap) {

    fun add(newDebtValue: NewDebtValue): Debtor {
        return Debtor(id, debtsMap.add(newDebtValue.debtorId, DebtValue(newDebtValue.value, newDebtValue.now)))
    }

}