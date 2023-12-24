package io.github.derui.pegen.core.parsing

import io.github.derui.pegen.core.support.Result

/**
 * A type of parsing result
 */
sealed interface ParsingResult<V>

/**
 * [Parsed] holds a result of parse of an expression.
 */
data class Parsed<V>(val result: Result<V, ErrorInfo>) : ParsingResult<V>

/**
 * [NoParse] is marker class for that did not parse yet.
 */
class NoParse<V> : ParsingResult<V> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
