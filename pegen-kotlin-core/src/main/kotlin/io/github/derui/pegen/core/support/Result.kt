package io.github.derui.pegen.core.support

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * A sealed class for result
 */
sealed class Result<out T, out E>

/**
 * Ok is success in any computation
 */
class Ok<out T>(val value: T) : Result<T, Nothing>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ok<*>

        return value == other.value
    }

    override fun hashCode(): Int = value?.hashCode() ?: 0

    override fun toString(): String {
        return "Ok($value)"
    }
}

/**
 * Err is failure in any computation
 */
class Err<out E>(val error: E) : Result<Nothing, E>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Err<*>

        return error == other.error
    }

    override fun hashCode(): Int {
        return error?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Err($error)"
    }
}

/**
 * get value if [this] is [Ok], otherwise throw [IllegalStateException].
 */
fun <T, E> Result<T, E>.get(): T {
    return when (this) {
        is Ok -> this.value
        is Err -> throw IllegalStateException("Result is Err, ${this.error}")
    }
}

/**
 * get value if [this] is [Ok], otherwise return null.
 */
@OptIn(ExperimentalContracts::class)
fun <T, E> Result<T, E>.getOrNull(): T? {
    contract {
        returnsNotNull() implies (this@getOrNull is Ok<T>)
        returns(null) implies (this@getOrNull is Err<E>)
    }

    return when (this) {
        is Ok -> this.value
        is Err -> null
    }
}

/**
 * operate [f] if [this] is [Ok].
 */
@OptIn(ExperimentalContracts::class)
inline infix fun <T, T2, E> Result<T, E>.map(f: (T) -> T2): Result<T2, E> {
    contract {
        callsInPlace(f, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> Ok(f(value))
        is Err -> this
    }
}

/**
 * operate [f] if [this] is [Ok].
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, T2, E> Result<T, E>.flatMap(f: (T) -> Result<T2, E>): Result<T2, E> {
    contract {
        callsInPlace(f, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> f(this.value)
        is Err -> Err(this.error)
    }
}

/**
 * fold [f] if [this] is [Ok], otherwise fold [e].
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, T2, E> Result<T, E>.fold(
    f: (T) -> T2,
    e: (E) -> T2,
): T2 {
    contract {
        callsInPlace(f, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
        callsInPlace(e, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
    }

    return when (this) {
        is Ok -> f(this.value)
        is Err -> e(this.error)
    }
}
