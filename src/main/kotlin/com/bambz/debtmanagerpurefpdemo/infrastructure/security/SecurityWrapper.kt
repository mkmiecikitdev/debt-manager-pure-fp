package com.bambz.debtmanagerpurefpdemo.infrastructure.security

import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import com.bambz.debtmanagerpurefpdemo.domain.errors.UnauthorizedError
import com.bambz.debtmanagerpurefpdemo.domain.users.Role
import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.Constants.AUTH_HEADER_KEY
import io.vavr.collection.Set
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class SecurityWrapper(private val jwtService: JwtService) {

    fun secure(req: ServerRequest, roles: Set<Role>, action: (ServerRequest, UserDto) -> Mono<ServerResponse>): Mono<ServerResponse> {
        return jwtService.getUserData(req.headers().header(AUTH_HEADER_KEY)[0])
                .map {
                    if (hasRoles(it.roles, roles))
                        action(req, it)
                    else
                        unauthorized()
                }.getOrElse { unauthorized() }
    }

    private fun hasRoles(inPayload: Set<Role>, roles: Set<Role>): Boolean {
        return inPayload.containsAll(roles)
    }

    private fun unauthorized(): Mono<ServerResponse> {
        return ServerResponse.status(resolveStatus(UnauthorizedError)).bodyValue(UnauthorizedError)
    }

    private fun resolveStatus(appError: AppError): HttpStatus {
        return when (appError) {
            is UnknownError -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.BAD_REQUEST
        }
    }
}