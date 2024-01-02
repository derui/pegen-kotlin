package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import java.util.UUID

/**
 * Syntax runner interface.
 */
internal class PegExpressionMiniParser<T, TagType>(
    private val syntax: PegExpression<T, TagType>,
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

        val result =
            run {
                if (syntax.sequences.isEmpty()) {
                    return@run Err(source.errorOf("Empty sequence is not allowed."))
                }

                for (seq in syntax.sequences) {
                    when (val ret = PegSequenceMiniParser(seq, recorder).parse(source, context)) {
                        is Ok -> return@run ret
                        is Err -> continue
                    }
                }

                return@run Err(source.errorOf("No sequence matched."))
            }

        recorder.parsed(syntax, result)

        return result
    }

    override val syntaxId: UUID = syntax.id
}
