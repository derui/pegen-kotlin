package io.github.derui.pegen.core.lang

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
    infix fun constructAs(typeConstructor: () -> T) = PegDefinition(id, this, typeConstructor)
}
