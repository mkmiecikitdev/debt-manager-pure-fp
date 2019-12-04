package com.bambz.debtmanagerpurefpdemo

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade
import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsModule
import com.bambz.debtmanagerpurefpdemo.domain.users.UsersFacade
import com.bambz.debtmanagerpurefpdemo.domain.users.UsersModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

data class Modules(
		val usersModule: UsersModule = UsersModule(),
		val debtsModule: DebtsModule = DebtsModule()
)

interface App {
	val usersFacade: UsersFacade
	val debtsFacade: DebtsFacade
}

class InMemoryApp(
		private val modules: Modules = Modules(),
		override val usersFacade: UsersFacade = modules.usersModule.createInMemoryFacade(),
		override val debtsFacade: DebtsFacade = modules.debtsModule.createInMemoryFacade()
) : App


fun main() {
	Server().start(InMemoryApp())
}
