package com.bambz.debtmanagerpurefpdemo.domain.kernel

import com.bambz.debtmanagerpurefpdemo.domain.errors.AppError
import io.vavr.collection.List
import io.vavr.control.Either
import reactor.core.publisher.Mono

typealias MonoEither<T> = Mono<Either<AppError, T>>

typealias MonoList<T> = Mono<List<T>>

fun <T> Either<T, T>.merge() = this.getOrElseGet { it }
