package com.bambz.debtmanagerpurefpdemo.domain.debts

import io.vavr.collection.HashMap

class Debtor(val id: String, private val debtsMap: DebtsMap) {

    fun addChange(newDebtValue: NewDebtValue): Debtor {
        return Debtor(id, debtsMap.add(newDebtValue.debtorId, DebtValue(newDebtValue.value, newDebtValue.now)))
    }

    fun toDbObject() = DbObject(
            id,
            debtsMap.toDbObject(),
            debtsMap.toDbUserSet()
    )

    companion object {

        fun new(id: String) = Debtor(id, DebtsMap(HashMap.empty()))

        fun fromDbObject(dbObject: DbObject) = Debtor(dbObject.id, DebtsMap.fromDbObject(dbObject.map))

    }

    data class DbObject(
            val id: String,
            val map: Map<String, List<DebtValue>>,
            val users: Set<String>
    )

}


