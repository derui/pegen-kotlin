package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result

/**
 * Syntax runner interface.
 */
class PegExpressionRunner<T, TagType>(private val syntax: PegExpression<T, TagType>) : SyntaxRunner<T, TagType>() {
    /**
     * Run the primary
     */
    override fun run(
        source: ParserSource,
        context: ParserContext<T, TagType>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        if (syntax.sequences.isEmpty()) {
            return Err(source.errorOf("Empty sequence is not allowed."))
        }

        for (seq in syntax.sequences) {
            when (val ret = PegSequenceRunner(seq).run(source, context)) {
                is Ok -> return ret
                is Err -> continue
            }
        }

        return Err(source.errorOf("No sequence matched."))
    }
}
