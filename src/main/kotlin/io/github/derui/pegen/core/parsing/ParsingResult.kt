package io.github.derui.pegen.core.parsing

import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result

/**
 * A type of parsing result
 */
sealed interface ParsingResult<V> {
    companion object {
        /**
         * Create a [Parsed] instance.
         */
        fun <V> parsed(result: V): ParsingResult<V> = Parsed(Ok(result))

        /**
         * Create a [Parsed] instance.
         */
        fun <V> parsed(result: ErrorInfo): ParsingResult<V> = Parsed(Err(result))

        /**
         * Create a [NoParse] instance.
         */
        fun <V> noParse(): ParsingResult<V> = NoParse()
    }
}

/**
 * [Parsed] holds a result of parse of an expression.
 */
class Parsed<V> internal constructor(val result: Result<V, ErrorInfo>) : ParsingResult<V>

/**
 * [NoParse] is marker class for that did not parse yet.
 */
class NoParse<V> internal constructor() : ParsingResult<V> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
