package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg suffix is a suffix of peg primary
 */
sealed class PegSuffix<T>(
    private val primary: PegPrimary<T>,
) : PegSyntax<T>

/**
 * This suffix is a PEG's [*] suffix
 */
class PegStarSuffix<T> internal constructor(primary: PegPrimary<T>, override val id: UUID) : PegSuffix<T>(primary)

/**
 * This suffix is a PEG's [+] suffix
 */
class PegPlusSuffix<T> internal constructor(primary: PegPrimary<T>, override val id: UUID) : PegSuffix<T>(primary)

/**
 * This suffix is a PEG's [?] suffix
 */
class PegQuestionSuffix<T> internal constructor(primary: PegPrimary<T>, override val id: UUID) : PegSuffix<T>(primary)

/**
 * This suffix is marker class for no suffix
 */
class PegNakedSuffix<T> internal constructor(primary: PegPrimary<T>, override val id: UUID) : PegSuffix<T>(primary)
