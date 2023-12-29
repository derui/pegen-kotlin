package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.support.Result

/**
 * Syntax runner interface.
 */
sealed class SyntaxRunner<T> {
    /**
     * Run a syntax in context
     */
    abstract fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo>
}
