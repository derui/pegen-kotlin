package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.dsl.support.DefaultSyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParserContext
import io.github.derui.pegen.core.parser.ParserSource
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.parser.PegExpressionMiniParser
import io.github.derui.pegen.core.support.Result

/**
 * A parser generator with PEG DSL
 */
object Generator {
    /**
     * invoke [init] with [PegDsl] and return [PegExpression] that is generated by [init].
     */
    operator fun <V, TagType> invoke(init: PegDsl<V, TagType>.() -> PegExpression<V, TagType>): Parser<V> {
        val generator = DefaultSyntaxIdentifierGenerator()

        val expr = PegDsl<V, TagType>(generator).init()

        return GeneratedParser(expr)
    }

    private class GeneratedParser<V, TagType>(
        private val syntax: PegExpression<V, TagType>,
    ) : Parser<V> {
        override fun parse(input: String): Result<ParsingResult<V>, ErrorInfo> {
            val source = ParserSource.newWith(input)
            val context = ParserContext.new(syntax)

            return PegExpressionMiniParser(syntax).parse(source, context)
        }
    }
}
