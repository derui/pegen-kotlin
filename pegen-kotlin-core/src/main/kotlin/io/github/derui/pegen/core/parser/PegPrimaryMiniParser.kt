package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
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
import java.util.UUID

/**
 * Syntax runner interface.
 */
sealed class PegPrimaryMiniParser<T, TagType> : MiniParser<T, TagType>() {
    companion object {
        /**
         * Run the primary
         */
        internal fun <T, TagType> run(
            primary: PegPrimary<T, TagType>,
            source: ParserSource,
            context: ParserContext<T, TagType>,
            recorder: DebuggingInfoRecorder,
        ) = when (primary) {
            is PegClassPrimary -> PegClassPrimaryMiniParser(primary, recorder).parse(source, context)
            is PegDotPrimary -> PegDotPrimaryMiniParser(primary, recorder).parse(source, context)
            is PegGroupPrimary -> PegGroupPrimaryMiniParser(primary, recorder).parse(source, context)
            is PegIdentifierPrimary -> PegIdentifierPrimaryMiniParser(primary, recorder).parse(source, context)
            is PegLiteralPrimary -> PegLiteralPrimaryMiniParser(primary, recorder).parse(source, context)
        }
    }

    /**
     * private runner of [PegDotPrimary]
     */
    private class PegDotPrimaryMiniParser<T, TagType>(
        private val primary: PegDotPrimary<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = primary.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(primary)

            return source.readChar().map {
                val result = ParsingResult.rawOf<T>(it.first.toString(), it.second)
                primary.tag?.let { tag -> context.tagging(tag, result) }
                result
            }.run {
                recorder.parsed(primary, this)
                this
            }
        }
    }

    /**
     * private runner of [PegLiteralPrimary]
     */
    private class PegLiteralPrimaryMiniParser<T, TagType>(
        private val primary: PegLiteralPrimary<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(primary)

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
            }.run {
                recorder.parsed(primary, this)
                this
            }
        }

        override val syntaxId: UUID = primary.id
    }

    /**
     * parser runner of [PegClassPrimary]
     */
    private class PegClassPrimaryMiniParser<T, TagType>(
        private val primary: PegClassPrimary<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = primary.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(primary)

            return source.readChar().flatMap { (ch, rest) ->
                if (ch !in primary.cls) {
                    Err(source.errorOf("$ch is not contained in $this"))
                } else {
                    val result = ParsingResult.rawOf<T>(ch.toString(), rest)
                    primary.tag?.let { tag -> context.tagging(tag, result) }
                    Ok(result)
                }
            }.run {
                recorder.parsed(primary, this)
                this
            }
        }
    }

    /**
     * parser runner of [PegGroupPrimary]
     */
    private class PegGroupPrimaryMiniParser<T, TagType>(
        private val primary: PegGroupPrimary<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = primary.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(primary)

            return PegExpressionMiniParser(primary.expression, recorder).parse(source, context).map {
                primary.tag?.let { tag -> context.tagging(tag, it) }
                it
            }.run {
                recorder.parsed(primary, this)
                this
            }
        }
    }

    /**
     * parser runner of [PegIdentifierPrimary]
     */
    private class PegIdentifierPrimaryMiniParser<T, TagType>(
        private val primary: PegIdentifierPrimary<T, TagType>,
        private val recorder: DebuggingInfoRecorder,
    ) : PegPrimaryMiniParser<T, TagType>() {
        override val syntaxId: UUID = primary.id

        override fun parse(
            source: ParserSource,
            context: ParserContext<T, TagType>,
        ): Result<ParsingResult<T>, ErrorInfo> {
            recorder.startParse(primary)

            return PegDefinitionMiniParser(primary.identifier.provide(), recorder).parse(source, context).map {
                primary.tag?.let { tag -> context.tagging(tag, it) }
                it
            }.run {
                recorder.parsed(primary, this)
                this
            }
        }
    }
}
