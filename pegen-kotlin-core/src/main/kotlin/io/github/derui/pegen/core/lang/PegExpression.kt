package io.github.derui.pegen.core.lang

import io.github.derui.pegen.core.dsl.support.PegDefinitionProvider
import io.github.derui.pegen.core.parser.ParserContext
import java.util.UUID

/**
 * A class for PEG expression
 */
class PegExpression<T, TagType> internal constructor(
    internal val sequences: List<PegSequence<T, TagType>>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSyntax<T, TagType> {
    /**
     * Construct this expression as
     */
    infix fun constructAs(typeConstructor: (ParserContext<T, TagType>) -> T) =
        object : PegDefinitionProvider<T, TagType> {
            override fun provide(): PegDefinition<T, TagType> {
                return PegDefinition(id, this@PegExpression, typeConstructor)
            }
        }

    override fun toString(): String {
        return sequences.joinToString(" / ") { it.toString() }
    }
}
