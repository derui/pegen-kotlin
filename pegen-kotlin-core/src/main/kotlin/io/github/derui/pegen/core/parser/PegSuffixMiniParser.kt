package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegPlusSuffix
import io.github.derui.pegen.core.lang.PegQuestionSuffix
import io.github.derui.pegen.core.lang.PegStarSuffix
import io.github.derui.pegen.core.lang.PegSuffix
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.flatMap
import io.github.derui.pegen.core.support.fold
import java.util.UUID

/**
 * Syntax runner interface.
 */
sealed class PegSuffixMiniParser<T, TagType> : MiniParser<T, TagType>() {
    companion object {
        /**
         * Run the primary
         */
        internal fun <T, TagType> run(
            syntax: PegSuffix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
            recorder: DebuggingInfoRecorder,
        ) = when (syntax) {
            is PegNakedSuffix -> PegNakedSuffixMiniParser(syntax, recorder).parse(source, context)
            is PegPlusSuffix -> PegPlusSuffixMiniParser(syntax, recorder).parse(source, context)
            is PegQuestionSuffix -> PegQuestionSuffixMiniParser(syntax, recorder).parse(source, context)
            is PegStarSuffix -> PegStarSuffixMiniParser(syntax, recorder).parse(source, context)
        }
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegNakedSuffixMiniParser<T, TagType>(
        private val suffix: PegNakedSuffix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return run(suffix.primary, source, context, recorder)
        }
    }

    /**
     * private runner of [PegPlusSuffix]
     */
    private class PegPlusSuffixMiniParser<T, TagType>(
        private val suffix: PegPlusSuffix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(suffix)

            return run(suffix.primary, source, context, recorder).flatMap {
                recurse(source, it.restSource, context)
            }.run {
                recorder.parsed(suffix, this)
                this
            }
        }

        private fun recurse(
            originalSource: ParserSource,
            recursiveSource: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return run(suffix.primary, recursiveSource, context, recorder).fold(
                { recurse(originalSource, it.restSource, context) },
            ) {
                val result = ParsingResult.noValueOf<T>(originalSource..recursiveSource, recursiveSource)
                suffix.tag?.run { context.tagging(this, result) }
                Ok(result)
            }
        }
    }

    /**
     * private runner of [PegStarSuffix]
     */
    private class PegStarSuffixMiniParser<T, TagType>(
        private val suffix: PegStarSuffix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            // define recursive function for plus
            recorder.startParse(suffix)

            fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                return run(suffix.primary, recursiveSource, context, recorder).fold({ recurse(it.restSource) }) {
                    val result = ParsingResult.noValueOf<T>(source..recursiveSource, recursiveSource)
                    suffix.tag?.run { context.tagging(this, result) }
                    Ok(result)
                }
            }

            return recurse(source).run {
                recorder.parsed(suffix, this)
                this
            }
        }
    }

    /**
     * private runner of [PegQuestionSuffix]
     */
    private class PegQuestionSuffixMiniParser<T, TagType>(
        private val suffix: PegQuestionSuffix<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(suffix)

            return run(suffix.primary, source, context, recorder).fold({
                suffix.tag?.run { context.tagging(this, it) }
                Ok(it)
            }, {
                val result = ParsingResult.noValueOf<T>("", source)
                suffix.tag?.run { context.tagging(this, result) }
                Ok(result)
            }).run {
                recorder.parsed(suffix, this)
                this
            }
        }
    }
}
