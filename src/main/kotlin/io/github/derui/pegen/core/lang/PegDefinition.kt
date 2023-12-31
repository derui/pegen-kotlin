package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * No tag version [PegExpression]. This class is used with type constructor of [T]
 */
class PegDefinition<T, TagType> internal constructor(
    override val id: UUID,
    internal val expression: PegExpression<T, TagType>,
    private val typeConstructor: () -> T,
) : PegSyntax<T, TagType> {
    override val tag: Nothing? = null
}
