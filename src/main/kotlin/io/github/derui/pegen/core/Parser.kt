package io.github.derui.pegen.core

import io.github.derui.pegen.core.debug.DebugPrinter
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.support.Result

/**
 * A interface for parser with parser syntax
 */
sealed interface Parser<T> {
    /**
     * Parse the input string and return the result.
     */
    fun parse(input: String): Result<ParsingResult<T>, ErrorInfo>

    /**
     * Get the debug printer of this parser.
     *
     * If a parser does not support debug, it should return [DebugPrinter] that does nothing.
     */
    fun printer(): DebugPrinter
}
