package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.support.Result
import java.util.UUID

/**
 * A parser interface for a syntax.
 */
sealed class MiniParser<T, TagType> {
    /**
     * The syntax id that is based of parser implementation
     */
    abstract val syntaxId: UUID

    /**
     * Run a syntax in context
     */
    abstract fun parse(
        source: ParserSource,
        context: ParserContext<T, TagType>,
    ): Result<ParsingResult<T>, ErrorInfo>
}
