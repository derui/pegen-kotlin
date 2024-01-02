package io.github.derui.pegen.core

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
}
