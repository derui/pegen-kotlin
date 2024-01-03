package io.github.derui.pegen.core.debug

import io.github.derui.pegen.core.lang.PegSyntax
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.support.Result

/**
 * An interface for recording debug information
 */
internal interface DebuggingInfoRecorder {
    /**
     * record an event with result of syntax
     */
    fun cacheHit(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    )

    /**
     * record an event with result of syntax
     */
    fun parsed(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    )

    /**
     * record an event without result of syntax
     */
    fun startParse(syntax: PegSyntax<*, *>)
}
