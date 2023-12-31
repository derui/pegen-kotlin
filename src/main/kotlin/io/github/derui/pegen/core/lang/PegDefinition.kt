package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * No tag version [PegExpression]. This class is used with type constructor of [T]
 */
class PegDefinition<T> internal constructor(
    override val id: UUID,
    private val sequences: List<PegSequence<T, *>>,
    private val typeConstructor: () -> T,
) : PegSyntax<T, Nothing> {
    override val tag: Nothing? = null
}
