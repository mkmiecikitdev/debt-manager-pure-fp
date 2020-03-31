package com.bambz.debtmanagerpurefpdemo.domain.kernel

import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import io.vavr.collection.List
import io.vavr.control.Either
import reactor.core.publisher.Mono

typealias Attempt<T> = Either<AppError, T>

typealias MonoEither<T> = Mono<Attempt<T>>

typealias MonoList<T> = Mono<List<T>>

fun <T> Either<T, T>.merge() = this.getOrElseGet { it }
