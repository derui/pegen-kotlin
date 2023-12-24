package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A class for PEG expression
 */
class PegExpression<T, TagType> internal constructor(
    override val id: UUID,
    private val sequeces: List<PegSequence<T, TagType>>,
    override val tag: TagType? = null,
) : PegSyntax<T, TagType>

/**
 * No tag version [PegExpression]. This class is used with type constructor of [T]
 */
class PegExpressionWithoutTag<T> internal constructor(
    override val id: UUID,
    private val sequeces: List<PegSequence<T, *>>,
    private val typeConstructor: () -> T,
) : PegSyntax<T, Nothing> {
    override val tag: Nothing? = null
}
