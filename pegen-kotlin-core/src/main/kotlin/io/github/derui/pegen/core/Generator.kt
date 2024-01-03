package io.github.derui.pegen.core

import io.github.derui.pegen.core.debug.DebugPrinter
import io.github.derui.pegen.core.debug.DebuggingInfoRecorder
import io.github.derui.pegen.core.debug.DebuggingInfoRecorderImpl
import io.github.derui.pegen.core.debug.NullDebuggingInfoRecorder
import io.github.derui.pegen.core.dsl.support.PegDefinitionProvider
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParserContext
import io.github.derui.pegen.core.parser.ParserSource
import io.github.derui.pegen.core.parser.ParsingResult
import io.github.derui.pegen.core.parser.PegDefinitionMiniParser
import io.github.derui.pegen.core.support.Result

/**
 * Parser generator with syntax that is defined by PEG DSL
 */
class Generator(private val option: GeneratorOption = GeneratorOption.default) {
    /**
     * Generate a parser from [definition] with [option]
     */
    fun <V, TagType> generateParserFrom(definition: PegDefinitionProvider<V, TagType>): Parser<V> {
        return GeneratedParser(definition.provide(), option)
    }

    private class GeneratedParser<V, TagType>(
        private val syntax: PegDefinition<V, TagType>,
        option: GeneratorOption,
    ) : Parser<V> {
        private val recorder: DebuggingInfoRecorder = if (option.debug) DebuggingInfoRecorderImpl() else NullDebuggingInfoRecorder()

        override fun parse(input: String): Result<ParsingResult<V>, ErrorInfo> {
            val source = ParserSource.newWith(input)
            val context = ParserContext.new(syntax)

            return PegDefinitionMiniParser(syntax, recorder).parse(source, context)
        }

        override fun printer(): DebugPrinter {
            return recorder as DebugPrinter
        }
    }
}
