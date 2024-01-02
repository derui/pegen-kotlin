package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg sequence is a sequence of peg prefix
 */
class PegSequence<T, TagType> internal constructor(
    internal val prefixes: List<PegPrefix<T, TagType>>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSyntax<T, TagType> {
    override fun toString(): String {
        return prefixes.joinToString(" ") { it.toString() }
    }
}
