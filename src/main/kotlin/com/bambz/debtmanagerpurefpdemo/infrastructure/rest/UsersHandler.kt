package com.bambz.debtmanagerpurefpdemo.infrastructure.rest

import com.bambz.debtmanagerpurefpdemo.domain.users.UsersFacade
import com.bambz.debtmanagerpurefpdemo.domain.users.api.NewUserDto
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.ServerResponseCreator
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

class UsersHandler(private val usersFacade: UsersFacade, private val serverResponseCreator: ServerResponseCreator) {

    fun routes() = router {
        "/users".nest {
            POST("/user", this@UsersHandler::addUser)
            POST("/admin", this@UsersHandler::addAdmin)
            POST("/login", this@UsersHandler::login)
        }
    }

    private fun addUser(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<NewUserDto>().flatMap {
            serverResponseCreator.fromUserData { usersFacade.addUser(it) }
        }
    }

    private fun addAdmin(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<NewUserDto>().flatMap {
            serverResponseCreator.fromUserData { usersFacade.addAdmin(it) }
        }
    }

    private fun login(req: ServerRequest): Mono<ServerResponse> {
        return req.bodyToMono<NewUserDto>().flatMap {
            serverResponseCreator.fromUserData { usersFacade.login(it) }
        }
    }
}