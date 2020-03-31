package com.bambz.debtmanagerpurefpdemo

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade
import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsModule
import com.bambz.debtmanagerpurefpdemo.domain.users.UsersFacade
import com.bambz.debtmanagerpurefpdemo.domain.users.UsersModule

data class Modules(
        val debtsModule: DebtsModule = DebtsModule(),
        val usersModule: UsersModule = UsersModule()
)

interface App {
    val usersFacade: UsersFacade
    val debtsFacade: DebtsFacade
}

class InMemoryApp(
        private val modules: Modules = Modules(),
        override val debtsFacade: DebtsFacade = modules.debtsModule.createInMemoryFacade(),
        override val usersFacade: UsersFacade = modules.usersModule.createInMemoryFacade(debtsFacade)
) : App


fun main() {
    Server().start(InMemoryApp())
}
