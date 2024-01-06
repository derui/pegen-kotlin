package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegSyntax
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import java.util.UUID

/**
 * A context for parsing.
 */
class ParserContext<T, TagType> private constructor(
    private val cache: PackratState<T>,
) {
    companion object {
        internal fun <T, TagType> new(syntax: PegSyntax<T, TagType>) =
            ParserContext<T, TagType>(PackratState.from()).also {
                it.currentSyntaxId = syntax.id
            }
    }

    /**
     * current syntax id in current context
     */
    lateinit var currentSyntaxId: UUID
        private set

    /**
     * Map of tag that is build in [PegDefinition] and [PegExpression] and its [PegSyntax].
     */
    private val tags = mutableMapOf<TagType, TaggedResult<T>>()

    /**
     * Get parsed result from cache if exists, or parse and cache it.
     */
    fun cacheIfAbsent(
        parserSource: ParserSource,
        parser: (ParserSource, ParserContext<T, TagType>) -> Result<ParsingResult<T>, ErrorInfo>,
    ): Result<ParsingResult<T>, ErrorInfo> {
        return when (val cached = cache.get(currentSyntaxId, parserSource.currentIndex)) {
            is ParserResultCache.NoParse -> {
                val result = parser(parserSource, this)
                when (result) {
                    is Ok -> cache.putParsed(currentSyntaxId, parserSource.currentIndex, result.value)
                    is Err -> cache.putError(currentSyntaxId, parserSource.currentIndex, result.error)
                }
                result
            }

            is ParserResultCache.Parsed -> cached.result
        }
    }

    /**
     * Clone this context with next syntax. Cloned context will have same cache.
     */
    fun newWith(syntax: PegSyntax<T, TagType>): ParserContext<T, TagType> =
        ParserContext<T, TagType>(cache).also {
            it.currentSyntaxId = syntax.id
        }

    /**
     * Register a tag and its syntax.
     */
    fun tagging(
        tag: TagType,
        result: ParsingResult<T>,
    ) {
        val list = tags.computeIfAbsent(tag) { TaggedResult.new() }
        tags[tag] = list + result
    }

    /**
     * Get tagged result
     */
    fun tagged(tag: TagType): TaggedResult<T> {
        return tags[tag] ?: TaggedResult.new()
    }
}
