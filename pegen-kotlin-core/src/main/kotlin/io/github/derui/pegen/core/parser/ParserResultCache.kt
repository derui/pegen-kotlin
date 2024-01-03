package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result

/**
 * A type of caching result of parse.
 */
sealed class ParserResultCache<V> private constructor() {
    companion object {
        /**
         * Create a [Parsed] instance.
         */
        fun <V> parsed(result: V): ParserResultCache<V> = Parsed(Ok(result))

        /**
         * Create a [Parsed] instance.
         */
        fun <V> parsed(result: ErrorInfo): ParserResultCache<V> = Parsed(Err(result))

        /**
         * Create a [NoParse] instance.
         */
        fun <V> noParse(): ParserResultCache<V> = NoParse()
    }

    /**
     * [Parsed] holds a result of parse of an expression.
     */
    class Parsed<V> internal constructor(val result: Result<V, ErrorInfo>) : ParserResultCache<V>()

    /**
     * [NoParse] is marker class for that did not parse yet.
     */
    class NoParse<V> internal constructor() : ParserResultCache<V>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int = javaClass.hashCode()
    }
}
