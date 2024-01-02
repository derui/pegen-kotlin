package io.github.derui.pegen.core.parser

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
        fun <T, TagType> run(
            syntax: PegSuffix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ) = when (syntax) {
            is PegNakedSuffix -> PegNakedSuffixMiniParser(syntax).parse(source, context)
            is PegPlusSuffix -> PegPlusSuffixMiniParser(syntax).parse(source, context)
            is PegQuestionSuffix -> PegQuestionSuffixMiniParser(syntax).parse(source, context)
            is PegStarSuffix -> PegStarSuffixMiniParser(syntax).parse(source, context)
        }
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegNakedSuffixMiniParser<T, TagType>(
        private val suffix: PegNakedSuffix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryMiniParser.run(suffix.primary, source, context)
        }
    }

    /**
     * private runner of [PegPlusSUffix]
     */
    private class PegPlusSuffixMiniParser<T, TagType>(
        private val suffix: PegPlusSuffix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryMiniParser.run(suffix.primary, source, context).flatMap {
                recurse(source, it.restSource, context)
            }
        }

        private fun recurse(
            originalSource: ParserSource,
            recursiveSource: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryMiniParser.run(suffix.primary, recursiveSource, context).fold(
                { recurse(originalSource, it.restSource, context) },
            ) {
                val result = ParsingResult.rawOf<T>(originalSource..recursiveSource, recursiveSource)
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
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            // define recursive function for plus
            fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                return PegPrimaryMiniParser.run(suffix.primary, recursiveSource, context).fold({ recurse(it.restSource) }) {
                    val result = ParsingResult.rawOf<T>(source..recursiveSource, recursiveSource)
                    suffix.tag?.run { context.tagging(this, result) }
                    Ok(result)
                }
            }

            return recurse(source)
        }
    }

    /**
     * private runner of [PegQuestionSuffix]
     */
    private class PegQuestionSuffixMiniParser<T, TagType>(
        private val suffix: PegQuestionSuffix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = suffix.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryMiniParser.run(suffix.primary, source, context).fold({
                suffix.tag?.run { context.tagging(this, it) }
                Ok(it)
            }) {
                val result = ParsingResult.rawOf<T>("", source)
                suffix.tag?.run { context.tagging(this, result) }
                Ok(result)
            }
        }
    }
}
