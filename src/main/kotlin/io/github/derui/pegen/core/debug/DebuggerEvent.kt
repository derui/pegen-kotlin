package io.github.derui.pegen.core.debug

import io.github.derui.pegen.core.lang.PegSyntax
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.support.Result

/**
 * record an event
 */
internal sealed class DebuggerEvent(
    val displayName: String,
    val syntax: PegSyntax<*, *>,
) {
    internal class StartParse(syntax: PegSyntax<*, *>) : DebuggerEvent("Start phase", syntax)

    internal class CacheHit(
        syntax: PegSyntax<*, *>,
        val result: Result<ParsingResult<*>, ErrorInfo>,
    ) : DebuggerEvent("Cache hit", syntax)

    internal class Parsed(
        syntax: PegSyntax<*, *>,
        val result: Result<ParsingResult<*>, ErrorInfo>,
    ) : DebuggerEvent("Parsed", syntax)
}
