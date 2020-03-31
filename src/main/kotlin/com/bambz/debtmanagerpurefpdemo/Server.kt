package com.bambz.debtmanagerpurefpdemo

import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.DebtsHandler
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.UsersHandler
import com.bambz.debtmanagerpurefpdemo.infrastructure.rest.helpers.ServerResponseCreator
import com.bambz.debtmanagerpurefpdemo.infrastructure.security.JwtService
import com.bambz.debtmanagerpurefpdemo.infrastructure.security.SecurityWrapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vavr.jackson.datatype.VavrModule
import org.reactivestreams.Publisher
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunctions
import reactor.netty.http.server.HttpServer
import reactor.netty.http.server.HttpServerRequest
import reactor.netty.http.server.HttpServerResponse
import java.util.function.BiFunction

class Server {

    fun start(app: App) {
        val objectMapper: ObjectMapper = ObjectMapper()
                .registerModule(KotlinModule())
                .registerModule(VavrModule())

        val jwtService = JwtService(objectMapper)
        val securityWrapper = SecurityWrapper(jwtService)
        val serverResponseCreator = ServerResponseCreator(objectMapper, jwtService)

        val httpHandler = RouterFunctions
                .toHttpHandler(
                        UsersHandler(app.usersFacade, serverResponseCreator).routes()
                                .and(DebtsHandler(app.debtsFacade, securityWrapper, serverResponseCreator).routes())

                )

        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        run(adapter)
    }

    private fun run(adapter: BiFunction<in HttpServerRequest, in HttpServerResponse, out Publisher<Void>>) {
        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(adapter)
                .bindNow()
                .onDispose()
                .block()
    }
}

