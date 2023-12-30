package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
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
sealed class PegPrimaryRunner<T> : SyntaxRunner<T>() {
    companion object {
        /**
         * Run the primary
         */
        fun <T, TagType : Tag> run(
            primary: PegPrimary<T, TagType>,
            context: ParserContext<T>,
        ) = when (primary) {
            is PegClassPrimary -> PegClassPrimaryRunner(primary).run(context)
            is PegDotPrimary -> PegDotPrimaryRunner(primary).run(context)
            is PegGroupPrimary -> TODO()
            is PegIdentifierPrimary -> TODO()
            is PegLiteralPrimary -> PegLiteralPrimaryRunner(primary).run(context)
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
    private class PegDotPrimaryRunner<T, TagType : Tag>(
        private val primary: PegDotPrimary<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return context.readChar().map {
                context.putRawIfTagged(primary.tag)

                ParsingResult.rawOf(context.parsed())
            }
        }
    }

    /**
     * private runner of [PegLiteralPrimary]
     */
    private class PegLiteralPrimaryRunner<T, TagType : Tag>(
        private val primary: PegLiteralPrimary<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return if (primary.literal.isEmpty()) {
                context.putRawIfTagged(primary.tag)

                Ok(ParsingResult.rawOf(context.parsed()))
            } else {
                fun recurse(index: Int): Result<Unit, ErrorInfo> {
                    return if (index == primary.literal.length) {
                        Ok(Unit)
                    } else {
                        val ch = primary.literal[index]
                        context.readChar().flatMap {
                            if (it == ch) {
                                recurse(index + 1)
                            } else {
                                Err(context.errorOf("Literal not matched: ${primary.literal}"))
                            }
                        }
                    }
                }

                recurse(0).map {
                    context.putRawIfTagged(primary.tag)
                    ParsingResult.rawOf(context.parsed())
                }
            }
        }
    }

    private class PegClassPrimaryRunner<T, TagType : Tag>(
        private val primary: PegClassPrimary<T, TagType>,
    ) : PegPrimaryRunner<T>() {
        /**
         * private runner of [PegClassPrimary]
         */
        override fun run(context: ParserContext<T>): Result<ParsingResult<T>, ErrorInfo> {
            return context.readChar().flatMap {
                if (it !in primary.cls) {
                    Err(context.errorOf("$it is not contained in $this"))
                } else {
                    context.putRawIfTagged(primary.tag)

                    Ok(ParsingResult.rawOf(context.parsed()))
                }
            }
        }
    }
}
