package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg prefix is a prefix of a peg suffix
 */
sealed class PegPrefix<T>(
    private val suffix: PegSuffix<T>,
) : PegSyntax<T>

/**
 * This prefix is a PEG's [&] prefix
 */
class PegAndPrefix<T> internal constructor(suffix: PegSuffix<T>, override val id: UUID) : PegPrefix<T>(suffix)

/**
 * This prefix is a PEG's [!] prefix
 */
class PegNotPrefix<T> internal constructor(suffix: PegSuffix<T>, override val id: UUID) : PegPrefix<T>(suffix)

/**
 * This prefix is marker class for no prefix
 */
class PegNakedPrefix<T> internal constructor(suffix: PegSuffix<T>, override val id: UUID) : PegPrefix<T>(suffix)
