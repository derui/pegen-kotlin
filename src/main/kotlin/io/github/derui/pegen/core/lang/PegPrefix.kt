package io.github.derui.pegen.core.lang

/**
 * A peg prefix is a prefix of a peg suffix
 */
sealed class PegPrefix(
    private val suffix: PegSuffix
)

/**
 * This prefix is a PEG's [&] prefix
 */
class PegAndPrefix internal constructor(suffix: PegSuffix) : PegPrefix(suffix)

/**
 * This prefix is a PEG's [!] prefix
 */
class PegNotPrefix internal constructor(suffix: PegSuffix) : PegPrefix(suffix)

/**
 * This prefix is marker class for no prefix
 */
class PegNakedPrefix internal constructor(suffix: PegSuffix) : PegPrefix(suffix)
