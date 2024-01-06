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
        internal fun <V> parsed(result: V): ParserResultCache<V> = Parsed(Ok(result))

        /**
         * Create a [Parsed] instance.
         */
        internal fun <V> parsed(result: ErrorInfo): ParserResultCache<V> = Parsed(Err(result))

        /**
         * Create a [NoParse] instance.
         */
        internal fun <V> noParse(): ParserResultCache<V> = NoParse()
    }

    /**
     * [Parsed] holds a result of parse of an expression.
     */
    internal class Parsed<V>(val result: Result<V, ErrorInfo>) : ParserResultCache<V>()

    /**
     * [NoParse] is marker class for that did not parse yet.
     */
    internal class NoParse<V> : ParserResultCache<V>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int = javaClass.hashCode()
    }
}
