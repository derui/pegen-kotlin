package io.github.derui.pegen.core.parser

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
        fun <T, TagType> run(
            syntax: PegPrefix<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ) = when (syntax) {
            is PegAndPrefix -> PegAndPrefixMiniParser(syntax).parse(source, context)
            is PegNakedPrefix -> PegNakedPrefixMiniParser(syntax).parse(source, context)
            is PegNotPrefix -> PegNotPrefixMiniParser(syntax).parse(source, context)
        }
    }

    /**
     * private runner of [PegNakedPrefix]
     */
    private class PegNakedPrefixMiniParser<T, TagType>(
        private val prefix: PegNakedPrefix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegSuffixMiniParser.run(prefix.suffix, source, context)
        }

        override val syntaxId: UUID = prefix.id
    }

    /**
     * private runner of [PegAndPrefix]
     */
    private class PegAndPrefixMiniParser<T, TagType>(
        private val prefix: PegAndPrefix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegSuffixMiniParser.run(prefix.suffix, source, context).flatMap {
                val result = ParsingResult.rawOf<T>("", source)
                Ok(result)
            }
        }

        override val syntaxId: UUID = prefix.id
    }

    /**
     * private runner of [PegNotPrefix]
     */
    private class PegNotPrefixMiniParser<T, TagType>(
        private val prefix: PegNotPrefix<T, TagType>,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            // define recursive function for plus
            fun recurse(recursiveSource: ParserSource): Result<ParsingResult<T>, ErrorInfo> {
                return PegSuffixMiniParser.run(prefix.suffix, recursiveSource, context).fold({
                    Err(source.errorOf("Can not apply not prefix"))
                }) {
                    val result = ParsingResult.rawOf<T>("", source)
                    Ok(result)
                }
            }

            return recurse(source)
        }

        override val syntaxId: UUID = prefix.id
    }
}
