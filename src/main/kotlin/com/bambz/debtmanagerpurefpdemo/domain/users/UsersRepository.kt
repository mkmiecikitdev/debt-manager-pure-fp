package com.bambz.debtmanagerpurefpdemo.domain.users

import reactor.core.publisher.Mono

interface UsersRepository {

    fun save(user: User): Mono<User>

    fun findByEmail(email: String): Mono<User>

}