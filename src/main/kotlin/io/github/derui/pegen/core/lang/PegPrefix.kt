package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg prefix is a prefix of a peg suffix
 */
sealed interface PegPrefix<T, TagType> : PegSyntax<T, TagType>

/**
 * This prefix is a PEG's [&] prefix
 */
class PegAndPrefix<T, TagType> internal constructor(
    private val suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>

/**
 * This prefix is a PEG's [!] prefix
 */
class PegNotPrefix<T, TagType> internal constructor(
    private val suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>

/**
 * This prefix is marker class for no prefix
 */
class PegNakedPrefix<T, TagType> internal constructor(
    private val suffix: PegSuffix<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrefix<T, TagType>
