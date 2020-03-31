package com.bambz.debtmanagerpurefpdemo.domain.users

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade

class UsersModule {

    fun createInMemoryFacade(debtsFacade: DebtsFacade): UsersFacade {
        return UsersFacade(InMemoryUsersRepository(), debtsFacade)
    }

}