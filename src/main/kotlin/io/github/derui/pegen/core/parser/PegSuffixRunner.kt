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
            context: ParserContext<T>,
        ) = when (syntax) {
            is PegNakedSuffix -> PegNakedSuffixRunner(syntax).run(context)
            is PegPlusSuffix -> PegPlusSuffixRunner(syntax).run(context)
            is PegQuestionSuffix -> PegQuestionSuffixRunner(syntax).run(context)
            is PegStarSuffix -> PegStarSuffixRunner(syntax).run(context)
        }
    }

    /**
     * A helper function to put parsed string as raw with [tag]
     */
    internal fun <T, R : Tag> ParserContext<T>.putRawIfTagged(tag: R?) {
        if (tag == null) {
            return
        }

        this.tagging(tag, ParsingResult.rawOf(parsed()))
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegNakedSuffixRunner<T, TagType : Tag>(
        private val suffix: PegNakedSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, context)
        }
    }

    /**
     * private runner of [PegPlusSUffix]
     */
    private class PegPlusSuffixRunner<T, TagType : Tag>(
        private val suffix: PegPlusSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, context).flatMap {
                fun recurse(): Result<ParsingResult<T>, ErrorInfo> {
                    return PegPrimaryRunner.run(suffix.primary, context).fold({ recurse() }) {
                        Ok(ParsingResult.rawOf(context.parsed()))
                    }
                }

                recurse()
            }
        }
    }

    /**
     * private runner of [PegStarSuffix]
     */
    private class PegStarSuffixRunner<T, TagType : Tag>(
        private val suffix: PegStarSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            fun recurse(): Result<ParsingResult<T>, ErrorInfo> {
                return PegPrimaryRunner.run(suffix.primary, context).fold({ recurse() }) {
                    Ok(ParsingResult.rawOf(context.parsed()))
                }
            }

            return recurse()
        }
    }

    /**
     * private runner of [PegQuestionSuffix]
     */
    private class PegQuestionSuffixRunner<T, TagType : Tag>(
        private val suffix: PegQuestionSuffix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return PegPrimaryRunner.run(suffix.primary, context).fold({ Ok(ParsingResult.rawOf(context.parsed())) }) {
                Ok(ParsingResult.rawOf(context.parsed()))
            }
        }
    }
}