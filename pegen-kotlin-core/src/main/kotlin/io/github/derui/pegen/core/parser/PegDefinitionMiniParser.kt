package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.map
import java.util.UUID

/**
 * Syntax runner interface.
 */
internal class PegDefinitionMiniParser<T, TagType>(
    private val syntax: PegDefinition<T, TagType>,
    private val recorder: DebuggingInfoRecorder,
) : MiniParser<T, TagType>() {
    /**
     * Run the primary
     */
    override fun parse(
        source: ParserSource,
        context: ParserContext<T, TagType>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        recorder.startParse(syntax)
        val newContext = context.newWith(syntax)

        var hit = true
        val result =
            newContext.cacheIfAbsent(source) { _source, _context ->
                hit = false

                PegExpressionMiniParser(syntax.expression, recorder).parse(_source, _context).map { result ->
                    ParsingResult.from(syntax.construct(_context), _source..result.restSource, result.restSource)
                }
            }

        if (hit) {
            recorder.cacheHit(syntax, result)
        } else {
            recorder.parsed(syntax, result)
        }

        return result
    }

    override val syntaxId: UUID = syntax.id
}
