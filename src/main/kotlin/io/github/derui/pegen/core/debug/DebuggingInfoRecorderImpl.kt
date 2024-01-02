package io.github.derui.pegen.core.debug

import io.github.derui.pegen.core.lang.PegSyntax
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result

/**
 * Debugging information of syntax/language
 */
internal class DebuggingInfoRecorderImpl internal constructor() : DebuggingInfoRecorder, DebugPrinter {
    /**
     * The stack of syntax that is being parsed
     */
    private val recordedEvents = mutableListOf<DebuggerEvent>()

    override fun print(): String {
        var indent = 0
        return buildString {
            recordedEvents.forEach {
                when (it) {
                    is DebuggerEvent.CacheHit -> indent -= 2
                    is DebuggerEvent.Parsed -> indent -= 2
                    is DebuggerEvent.StartParse -> indent += 2
                }

                append(" ".repeat(indent))
                append("[${it.displayName}] : `${it.syntax}'")

                val source =
                    when (it) {
                        is DebuggerEvent.CacheHit -> it.result
                        is DebuggerEvent.Parsed -> it.result
                        else -> null
                    }
                when (source) {
                    is Ok -> append(" => ${source.value} : rest => `${source.value.restSource}'")
                    is Err -> {
                        val message = source.error.messages.joinToString(";")
                        append(" => Error : `$message' at ${source.error.position}")
                    }

                    null -> Unit
                }
                appendLine()
            }
        }
    }

    override fun cacheHit(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    ) {
        recordedEvents.add(DebuggerEvent.CacheHit(syntax, result))
    }

    override fun parsed(
        syntax: PegSyntax<*, *>,
        result: Result<ParsingResult<*>, ErrorInfo>,
    ) {
        recordedEvents.add(DebuggerEvent.Parsed(syntax, result))
    }

    override fun startParse(syntax: PegSyntax<*, *>) {
        recordedEvents.add(DebuggerEvent.StartParse(syntax))
    }
}
