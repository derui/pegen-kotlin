package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegSyntax

/**
 * A context for parsing.
 */
class ParserContext<T> private constructor() {
    companion object {
        fun <T> new() = ParserContext<T>()
    }

    /**
     * Map of tag that is build in [PegDefinition] and [PegExpression] and its [PegSyntax].
     */
    private val tags = mutableMapOf<Tag, ParsingResult<T>>()

    /**
     * Register a tag and its syntax.
     */
    fun tagging(
        tag: Tag,
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
    fun tagged(tag: Tag): ParsingResult<T>? = tags[tag]
}
