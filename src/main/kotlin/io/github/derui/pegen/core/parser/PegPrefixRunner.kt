package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNotPrefix
import io.github.derui.pegen.core.lang.PegPrefix
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.flatMap
import io.github.derui.pegen.core.support.fold

/**
 * Syntax runner interface.
 */
sealed class PegPrefixRunner<T> : SyntaxRunner<T>() {
    companion object {
        /**
         * Run the primary
         */
        fun <T, TagType : Tag> run(
            syntax: PegPrefix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T>,
        ) = when (syntax) {
            is PegAndPrefix -> PegAndPrefixRunner(syntax).run(source, context)
            is PegNakedPrefix -> PegNakedPrefixRunner(syntax).run(source, context)
            is PegNotPrefix -> PegNotPrefixRunner(syntax).run(source, context)
        }
    }

    /**
     * private runner of [PegNakedPrefix]
     */
    private class PegNakedPrefixRunner<T, TagType : Tag>(
        private val prefix: PegNakedPrefix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegSuffixRunner.run(prefix.suffix, source, context)
        }
    }

    /**
     * private runner of [PegAndPrefix]
     */
    private class PegAndPrefixRunner<T, TagType : Tag>(
        private val prefix: PegAndPrefix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegSuffixRunner.run(prefix.suffix, source, context).flatMap {
                val result = ParsingResult.rawOf<T>("", source)
                Ok(result)
            }
        }
    }

    /**
     * private runner of [PegNotPrefix]
     */
    private class PegNotPrefixRunner<T, TagType : Tag>(
        private val prefix: PegNotPrefix<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            // define recursive function for plus
            fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                return PegSuffixRunner.run(prefix.suffix, recursiveSource, context).fold({
                    Err(source.errorOf("Can not apply not prefix"))
                }) {
                    val result = ParsingResult.rawOf<T>("", source)
                    Ok(result)
                }
            }

            return recurse(source)
        }
    }
}
