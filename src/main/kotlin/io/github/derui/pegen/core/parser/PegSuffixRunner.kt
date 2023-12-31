package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
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

/**
 * Syntax runner interface.
 */
sealed class PegSuffixRunner<T> : SyntaxRunner<T>() {
    companion object {
        /**
         * Run the primary
         */
        fun <T, TagType : Tag> run(
            syntax: PegSuffix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T>,
        ) = when (syntax) {
            is PegNakedSuffix -> PegNakedSuffixRunner(syntax).run(source, context)
            is PegPlusSuffix -> PegPlusSuffixRunner(syntax).run(source, context)
            is PegQuestionSuffix -> PegQuestionSuffixRunner(syntax).run(source, context)
            is PegStarSuffix -> PegStarSuffixRunner(syntax).run(source, context)
        }
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegNakedSuffixRunner<T, TagType : Tag>(
        private val suffix: PegNakedSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, source, context)
        }
    }

    /**
     * private runner of [PegPlusSUffix]
     */
    private class PegPlusSuffixRunner<T, TagType : Tag>(
        private val suffix: PegPlusSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, source, context).flatMap {
                // define recursive function for plus
                fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                    return PegPrimaryRunner.run(suffix.primary, recursiveSource, context).fold({ recurse(it.restSource) }) {
                        val result = ParsingResult.rawOf<T>(source..recursiveSource, recursiveSource)
                        suffix.tag?.run { context.tagging(this, result) }
                        Ok(result)
                    }
                }

                recurse(it.restSource)
            }
        }
    }

    /**
     * private runner of [PegStarSuffix]
     */
    private class PegStarSuffixRunner<T, TagType : Tag>(
        private val suffix: PegStarSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            // define recursive function for plus
            fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                return PegPrimaryRunner.run(suffix.primary, recursiveSource, context).fold({ recurse(it.restSource) }) {
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
    private class PegQuestionSuffixRunner<T, TagType : Tag>(
        private val suffix: PegQuestionSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, source, context).fold({
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
