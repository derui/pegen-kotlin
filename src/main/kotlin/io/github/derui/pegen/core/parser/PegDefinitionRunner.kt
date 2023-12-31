package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.map

/**
 * Syntax runner interface.
 */
class PegDefinitionRunner<T, TagType>(private val syntax: PegDefinition<T, TagType>) : SyntaxRunner<T, TagType>() {
    /**
     * Run the primary
     */
    override fun run(
        source: ParserSource,
        context: ParserContext<T, TagType>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        val newContext = ParserContext.new<T, TagType>()

        return PegExpressionRunner(syntax.expression).run(source, newContext).map { result ->
            ParsingResult.constructedAs(syntax.construct(newContext), result.restSource)
        }
    }
}
