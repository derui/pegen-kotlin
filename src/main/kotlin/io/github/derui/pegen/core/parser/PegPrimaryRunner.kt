package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegGroupPrimary
import io.github.derui.pegen.core.lang.PegIdentifierPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegPrimary
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.flatMap
import io.github.derui.pegen.core.support.map

/**
 * Syntax runner interface.
 */
sealed class PegPrimaryRunner<T, TagType> : SyntaxRunner<T, TagType>() {
    companion object {
        /**
         * Run the primary
         */
        fun <T, TagType> run(
            primary: PegPrimary<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ) = when (primary) {
            is PegClassPrimary -> PegClassPrimaryRunner(primary).run(source, context)
            is PegDotPrimary -> PegDotPrimaryRunner(primary).run(source, context)
            is PegGroupPrimary -> PegGroupPrimaryRunner(primary).run(source, context)
            is PegIdentifierPrimary -> PegIdentifierPrimaryRunner(primary).run(source, context)
            is PegLiteralPrimary -> PegLiteralPrimaryRunner(primary).run(source, context)
        }
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegDotPrimaryRunner<T, TagType>(
        private val primary: PegDotPrimary<T, TagType>,
    ) : PegPrimaryRunner<T, TagType>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return source.readChar().map {
                val result = ParsingResult.rawOf<T>(it.first.toString(), it.second)
                primary.tag?.let { tag -> context.tagging(tag, result) }
                result
            }
        }
    }

    /**
     * private runner of [PegLiteralPrimary]
     */
    private class PegLiteralPrimaryRunner<T, TagType>(
        private val primary: PegLiteralPrimary<T, TagType>,
    ) : PegPrimaryRunner<T, TagType>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return if (primary.literal.isEmpty()) {
                val result = ParsingResult.rawOf<T>("", source)
                primary.tag?.let { tag -> context.tagging(tag, result) }

                Ok(result)
            } else {
                var index = 0

                val (matched, rest) =
                    source.advanceWhile { ch ->
                        if (index < primary.literal.length && ch == primary.literal[index]) {
                            index++
                            true
                        } else {
                            false
                        }
                    }

                if (matched == primary.literal) {
                    val result = ParsingResult.rawOf<T>(matched, rest)
                    primary.tag?.let { tag -> context.tagging(tag, result) }

                    Ok(result)
                } else {
                    Err(source.errorOf("Unexpected literal"))
                }
            }
        }
    }

    /**
     * parser runner of [PegClassPrimary]
     */
    private class PegClassPrimaryRunner<T, TagType>(
        private val primary: PegClassPrimary<T, TagType>,
    ) : PegPrimaryRunner<T, TagType>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return source.readChar().flatMap { (ch, rest) ->
                if (ch !in primary.cls) {
                    Err(source.errorOf("$ch is not contained in $this"))
                } else {
                    val result = ParsingResult.rawOf<T>(ch.toString(), rest)
                    primary.tag?.let { tag -> context.tagging(tag, result) }
                    Ok(result)
                }
            }
        }
    }

    /**
     * parser runner of [PegGroupPrimary]
     */
    private class PegGroupPrimaryRunner<T, TagType>(
        private val primary: PegGroupPrimary<T, TagType>,
    ) : PegPrimaryRunner<T, TagType>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegExpressionRunner(primary.expression).run(source, context).map {
                primary.tag?.let { tag -> context.tagging(tag, it) }
                it
            }
        }
    }

    /**
     * parser runner of [PegIdentifierPrimary]
     */
    private class PegIdentifierPrimaryRunner<T, TagType>(
        private val primary: PegIdentifierPrimary<T, TagType>,
    ) : PegPrimaryRunner<T, TagType>() {
        override fun run(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            return PegDefinitionRunner(primary.identifier).run(source, context).map {
                primary.tag?.let { tag -> context.tagging(tag, it) }
                it
            }
        }
    }
}
