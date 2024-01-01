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
        val newContext = context.clone()

        return PegExpressionMiniParser(syntax.expression).parse(source, newContext).map { result ->
            ParsingResult.constructedAs(syntax.construct(newContext), result.restSource)
        }
    }

    override val syntaxId: UUID = syntax.id
}
