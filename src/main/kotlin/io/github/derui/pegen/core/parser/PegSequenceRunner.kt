package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result

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
        for (prefix in syntax.prefixes) {
            when (val ret = PegPrefixRunner.run(prefix, source, context)) {
                is Ok -> return ret
                is Err -> continue
            }
        }

        return Err(source.errorOf("Do not match any prefix."))
    }
}
