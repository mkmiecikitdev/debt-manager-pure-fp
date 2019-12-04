package com.bambz.debtmanagerpurefpdemo.infrastructure.rest

import com.bambz.debtmanagerpurefpdemo.domain.debts.DebtsFacade
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.AddDebtDto
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.ClearDebtDto
import com.bambz.debtmanagerpurefpdemo.domain.debts.api.MinusDebtDto
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
            POST("/minus", this@DebtsHandler::minusDebt)
            POST("/clear", this@DebtsHandler::clearDebt)
        }
    }

    private fun addDebt(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.secure(req, HashSet.of(Role.USER)) { request, userData ->
            request.bodyToMono<AddDebtDto>().flatMap {
                serverResponseCreator.fromMonoEither { debtsFacade.addDebt(userData.email, it) }
            }
        }
    }

    private fun minusDebt(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.secure(req, HashSet.of(Role.USER)) { request, userData ->
            request.bodyToMono<MinusDebtDto>().flatMap {
                serverResponseCreator.fromMonoEither { debtsFacade.minusDebt(userData.email, it) }
            }
        }
    }

    private fun clearDebt(req: ServerRequest): Mono<ServerResponse> {
        return securityWrapper.secure(req, HashSet.of(Role.USER, Role.ADMIN)) { request, _ ->
            request.bodyToMono<ClearDebtDto>().flatMap {
                serverResponseCreator.okFromMono { debtsFacade.clearDebt(it) }
            }
        }
    }
}