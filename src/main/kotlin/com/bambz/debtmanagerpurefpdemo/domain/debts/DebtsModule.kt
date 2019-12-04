package com.bambz.debtmanagerpurefpdemo.domain.debts

import com.bambz.debtmanagerpurefpdemo.domain.kernel.TimeServiceDefault

class DebtsModule {

    fun createInMemoryFacade(): DebtsFacade {
        return DebtsFacade(InMemoryDebtsRepository(), TimeServiceDefault())
    }

}