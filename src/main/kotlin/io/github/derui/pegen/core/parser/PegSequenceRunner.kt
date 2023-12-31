package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.get

/**
 * Syntax runner interface.
 */
class PegSequenceRunner<T, TagType : Tag>(private val syntax: PegSequence<T, TagType>) : SyntaxRunner<T>() {
    /**
     * Run the primary
     */
    override fun run(
        source: ParserSource,
        context: ParserContext<T>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        if (syntax.prefixes.isEmpty()) {
            return Err(source.errorOf("Empty sequence is not allowed."))
        }

        var result = ParsingResult.rawOf<T>("", source)
        for (prefix in syntax.prefixes) {
            when (val ret = PegPrefixRunner.run(prefix, source, context)) {
                is Ok -> result = ret.get()
                is Err -> return ret
            }
        }

        return Ok(ParsingResult.rawOf(source..result.restSource, result.restSource))
    }
}
