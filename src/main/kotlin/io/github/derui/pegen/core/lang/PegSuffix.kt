package io.github.derui.pegen.core.lang

/**
 * A peg suffix is a suffix of peg primary
 */
sealed class PegSuffix(
    private val primary: PegPrimary,
)

/**
 * This suffix is a PEG's [*] suffix
 */
class PegStarSuffix internal constructor(primary: PegPrimary) : PegSuffix(primary)

/**
 * This suffix is a PEG's [+] suffix
 */
class PegPlusSuffix internal constructor(primary: PegPrimary) : PegSuffix(primary)

/**
 * This suffix is a PEG's [?] suffix
 */
class PegQuestionSuffix internal constructor(primary: PegPrimary) : PegSuffix(primary)

/**
 * This suffix is marker class for no suffix
 */
class PegNakedSuffix internal constructor(primary: PegPrimary) : PegSuffix(primary)
