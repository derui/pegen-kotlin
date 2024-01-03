package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg prefix is a prefix of a peg suffix
 */
sealed class PegPrefix<T, TagType>(internal val suffix: PegSuffix<T, TagType>) : PegSyntax<T, TagType>

/**
 * This prefix is a PEG's [&] prefix
 */
class PegAndPrefix<T, TagType> internal constructor(
    suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>(suffix) {
    override fun toString(): String {
        return "&$suffix"
    }
}

/**
 * This prefix is a PEG's [!] prefix
 */
class PegNotPrefix<T, TagType> internal constructor(
    suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>(suffix) {
    override fun toString(): String {
        return "!$suffix"
    }
}

/**
 * This prefix is marker class for no prefix
 */
class PegNakedPrefix<T, TagType> internal constructor(
    suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>(suffix) {
    override fun toString(): String {
        return "$suffix"
    }
}
