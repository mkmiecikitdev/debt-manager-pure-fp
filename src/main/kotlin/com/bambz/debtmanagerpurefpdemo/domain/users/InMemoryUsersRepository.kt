package com.bambz.debtmanagerpurefpdemo.domain.users

import io.vavr.collection.HashMap
import io.vavr.collection.Map
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicReference

class InMemoryUsersRepository(private var users: AtomicReference<Map<String, User>> = AtomicReference(HashMap.empty())) : UsersRepository {

    override fun findByEmail(email: String): Mono<User> {
        return users.get()[email]
                .map { Mono.just(it) }
                .getOrElse { Mono.empty() }
    }

    override fun save(user: User): Mono<User> {
        users.updateAndGet { it.put(user.email, user) }
        return Mono.just(user)
    }

}