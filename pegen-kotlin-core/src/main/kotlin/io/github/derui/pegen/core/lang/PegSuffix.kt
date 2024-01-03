package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg suffix is a suffix of peg primary
 */
sealed class PegSuffix<T, TagType>(internal val primary: PegPrimary<T, TagType>) : PegSyntax<T, TagType>

/**
 * This suffix is a PEG's [*] suffix
 */
class PegStarSuffix<T, TagType> internal constructor(
    primary: PegPrimary<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSuffix<T, TagType>(primary) {
    override fun toString(): String {
        return "$primary*"
    }
}

/**
 * This suffix is a PEG's [+] suffix
 */
class PegPlusSuffix<T, TagType> internal constructor(
    primary: PegPrimary<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSuffix<T, TagType>(primary) {
    override fun toString(): String {
        return "$primary+"
    }
}

/**
 * This suffix is a PEG's [?] suffix
 */
class PegQuestionSuffix<T, TagType> internal constructor(
    primary: PegPrimary<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSuffix<T, TagType>(primary) {
    override fun toString(): String {
        return "$primary?"
    }
}

/**
 * This suffix is marker class for no suffix
 */
class PegNakedSuffix<T, TagType> internal constructor(
    primary: PegPrimary<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegSuffix<T, TagType>(primary) {
    override fun toString(): String {
        return "$primary"
    }
}
