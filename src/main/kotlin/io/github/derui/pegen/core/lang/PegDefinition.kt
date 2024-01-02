package io.github.derui.pegen.core.lang

import io.github.derui.pegen.core.parser.ParserContext
import java.util.UUID

/**
 * No tag version [PegExpression]. This class is used with type constructor of [T]
 */
class PegDefinition<T, TagType> internal constructor(
    override val id: UUID,
    internal val expression: PegExpression<T, TagType>,
    private val typeConstructor: (ParserContext<T, TagType>) -> T,
) : PegSyntax<T, TagType> {
    override val tag: Nothing? = null

    /**
     * Construct [T] from [ParserContext]
     */
    fun construct(context: ParserContext<T, TagType>) = typeConstructor(context)

    override fun toString(): String {
        return expression.toString()
    }
}
