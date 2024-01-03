package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNotPrefix
import io.github.derui.pegen.core.lang.PegPrefix
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.flatMap
import io.github.derui.pegen.core.support.fold
import java.util.UUID

/**
 * Syntax runner interface.
 */
sealed class PegPrefixMiniParser<T, TagType> : MiniParser<T, TagType>() {
    companion object {
        /**
         * Run the primary
         */
        internal fun <T, TagType> run(
            syntax: PegPrefix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
            recorder: DebuggingInfoRecorder,
        ) = when (syntax) {
            is PegAndPrefix -> PegAndPrefixMiniParser(syntax, recorder).parse(source, context)
            is PegNakedPrefix -> PegNakedPrefixMiniParser(syntax, recorder).parse(source, context)
            is PegNotPrefix -> PegNotPrefixMiniParser(syntax, recorder).parse(source, context)
        }
    }

    /**
     * private runner of [PegNakedPrefix]
     */
    private class PegNakedPrefixMiniParser<T, TagType>(
        private val prefix: PegNakedPrefix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegSuffixMiniParser.run(prefix.suffix, source, context, recorder)
        }

        override val syntaxId: UUID = prefix.id
    }

    /**
     * private runner of [PegAndPrefix]
     */
    private class PegAndPrefixMiniParser<T, TagType>(
        private val prefix: PegAndPrefix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(prefix)

            return PegSuffixMiniParser.run(prefix.suffix, source, context, recorder).flatMap {
                val result = ParsingResult.rawOf<T>("", source)
                Ok(result)
            }.run {
                recorder.parsed(prefix, this)
                this
            }
        }

        override val syntaxId: UUID = prefix.id
    }

    /**
     * private runner of [PegNotPrefix]
     */
    private class PegNotPrefixMiniParser<T, TagType>(
        private val prefix: PegNotPrefix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(prefix)

            return PegSuffixMiniParser.run(prefix.suffix, source, context, recorder).fold({
                Err(source.errorOf("Can not apply not prefix"))
            }) {
                val result = ParsingResult.rawOf<T>("", source)
                Ok(result)
            }.run {
                recorder.parsed(prefix, this)
                this
            }
        }

        override val syntaxId: UUID = prefix.id
    }
}
