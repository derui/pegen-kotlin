package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.map
import java.util.UUID

/**
 * Syntax runner interface.
 */
class PegDefinitionMiniParser<T, TagType>(private val syntax: PegDefinition<T, TagType>) : MiniParser<T, TagType>() {
    /**
     * Run the primary
     */
    override fun parse(
        source: ParserSource,
        context: ParserContext<T, TagType>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        val newContext = context.newWith(syntax)

        return newContext.cacheIfAbsent(source) { _source, _context ->
            PegExpressionMiniParser(syntax.expression).parse(_source, _context).map { result ->
                ParsingResult.constructedAs(syntax.construct(_context), result.restSource)
            }
        }
    }

    override val syntaxId: UUID = syntax.id
}
