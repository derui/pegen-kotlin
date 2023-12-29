package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A class for PEG expression
 */
class PegExpression<T, TagType> internal constructor(
    override val id: UUID,
    private val sequences: List<PegSequence<T, TagType>>,
    override val tag: TagType? = null,
) : PegSyntax<T, TagType> {
    /**
     * Construct this expression as
     */
    infix fun constructAs(typeConstructor: () -> T): PegDefinition<T> = PegDefinition(id, sequences, typeConstructor)
}

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