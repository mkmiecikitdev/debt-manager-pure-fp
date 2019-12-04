package com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers

import com.bambz.debtmanagerpurefpdemo.domain.kernel.MonoEither
import com.bambz.debtmanagerpurefpdemo.domain.users.api.UserDto
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.Constants.AUTH_HEADER_KEY
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.Constants.BEARER
import com.bambz.debtmanagerpurefpdemo.infrastructure.security.JwtService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class ServerResponseCreator(private val objectMapper: ObjectMapper, private val jwtService: JwtService) {

    fun <T : Any> okFromMono(mono: () -> Mono<T>): Mono<ServerResponse> {
        return mono().flatMap { result ->
            ServerResponse.ok().bodyValue(objectMapper.convertValue(result))
        }.switchIfEmpty(ServerResponse.notFound().build())
    }

    fun <T : Any> fromMonoEither(monoEither: () -> MonoEither<T>): Mono<ServerResponse> {
        return monoEither().flatMap {
            it.map { result ->
                ServerResponse.ok().bodyValue(objectMapper.convertValue(result))
            }.getOrElseGet { error ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(error)
            }
        }.switchIfEmpty(ServerResponse.notFound().build())
    }

    fun fromUserData(monoEither: () -> MonoEither<UserDto>): Mono<ServerResponse> {
        return monoEither().flatMap {
            it.map { result ->
                ServerResponse.ok()
                        .header(AUTH_HEADER_KEY, "$BEARER ${jwtService.generateJwt(result)}")
                        .bodyValue(objectMapper.convertValue(result))
            }.getOrElseGet { error ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                        .bodyValue(error)
            }
        }
    }
}