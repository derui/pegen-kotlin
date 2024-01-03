package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.get
import java.util.UUID

/**
 * Syntax runner interface.
 */
internal class PegSequenceMiniParser<T, TagType>(
    private val syntax: PegSequence<T, TagType>,
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
                if (syntax.prefixes.isEmpty()) {
                    return@run Err(source.errorOf("Empty sequence is not allowed."))
                }

                var result = ParsingResult.rawOf<T>("", source)
                for (prefix in syntax.prefixes) {
                    when (val ret = PegPrefixMiniParser.run(prefix, result.restSource, context, recorder)) {
                        is Ok -> result = ret.get()
                        is Err -> return@run ret
                    }
                }

                val sequenceResult = ParsingResult.rawOf<T>(source..result.restSource, result.restSource)
                syntax.tag?.let { tag -> context.tagging(tag, sequenceResult) }

                return@run Ok(sequenceResult)
            }

        recorder.parsed(syntax, result)
        return result
    }

    override val syntaxId: UUID = syntax.id
}
