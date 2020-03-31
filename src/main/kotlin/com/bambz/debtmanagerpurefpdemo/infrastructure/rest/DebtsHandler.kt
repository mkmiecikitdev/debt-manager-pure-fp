package com.bambz.debtmanagerpurefpdemo.infrastructure.rest

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.NewDebtValueForm
import com.bambz.debtmanagerpurefpdemo.domain.users.Role
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.ServerResponseCreator
import com.bambz.debtmanagerpurefpdemo.infrastructure.security.SecurityWrapper
import io.vavr.collection.HashSet
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class DebtsHandler(private val debtsFacade: DebtsFacade, private val securityWrapper: SecurityWrapper, private val serverResponseCreator: ServerResponseCreator) {

    fun routes() = router {
        "/debts".nest {
            POST("/add", this@DebtsHandler::addDebt)
        }
    }

    private fun addDebt(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.secure(req, HashSet.of(Role.USER)) { request, userData ->
            request.bodyToMono<NewDebtValueForm>().flatMap {
                serverResponseCreator.fromMonoEither { debtsFacade.addDebt(userData.email, it) }
            }
        }
    }

}