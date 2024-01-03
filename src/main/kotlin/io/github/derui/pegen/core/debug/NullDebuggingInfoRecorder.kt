package io.github.derui.pegen.core.debug

import io.github.derui.pegen.core.lang.PegSyntax
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.support.Result

/**
 * No-op version debugger
 */
internal class NullDebuggingInfoRecorder : DebuggingInfoRecorder, DebugPrinter {
    override fun cacheHit(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    ) {}

    override fun parsed(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    ) {}

    override fun startParse(syntax: PegSyntax<*, *>) {}

    override fun print(): String = ""
}
