package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegSyntax

/**
 * A context for parsing.
 */
class ParserContext<T, TagType> private constructor() {
    companion object {
        fun <T, TagType> new() = ParserContext<T, TagType>()
    }

    /**
     * Map of tag that is build in [PegDefinition] and [PegExpression] and its [PegSyntax].
     */
    private val tags = mutableMapOf<TagType, ParsingResult<T>>()

    /**
     * Register a tag and its syntax.
     */
    fun tagging(
        tag: TagType,
        result: ParsingResult<T>,
    ) {
        require(tag !in tags) {
            "Tag $tag is already registered. Please check your grammar."
        }

        tags[tag] = result
    }

    /**
     * Get tagged result
     */
    fun tagged(tag: TagType): ParsingResult<T>? = tags[tag]
}
