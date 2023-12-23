package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.lang.*

/**
 * Dsl for PEG
 */
class PegDsl {

    /**
     * Create a new [PegExpression] with the given [sequences]. This function implicitly expresses CHOICE.
     */
    fun exp(vararg sequences: PegSequence): PegExpression = PegExpression(sequences.toList())

    /**
     * Create a new [PegSequence] with the given [suffixes]. This function implicitly expresses SEQUENCE.
     */
    fun s(vararg prefixes: PegSequence): PegExpression = PegExpression(prefixes.toList())

    /**
     *  Create a new [PegAndPrefix] with the given [suffix].
     */
    fun and(suffix: PegSuffix): PegPrefix = PegAndPrefix(suffix)

    /**
     *  Create a new [PegNotPrefix] with the given [suffix].
     */
    fun not(suffix: PegSuffix): PegPrefix = PegNotPrefix(suffix)

    /**
     * Create a new [PegNakedPrefix] with the given [suffix].
     */
    fun np(suffix: PegSuffix): PegPrefix = PegNakedPrefix(suffix)

    /**
     * Create a new [PegSuffix] as a representation of [*] in PEG
     */
    fun many(primary: PegPrimary): PegSuffix = PegStarSuffix(primary)

    /**
     * Create a new [PegSuffix] as a representation of [+] in PEG
     */
    fun many1(primary: PegPrimary): PegSuffix = PegPlusSuffix(primary)

    /**
     * Create a new [PegSuffix] as a representation of [?] in PEG
     */
    fun opt(primary: PegPrimary): PegSuffix = PegQuestionSuffix(primary)

    /**
     * Create a new [PegSuffix] without any suffix
     */
    fun ns(primary: PegPrimary): PegSuffix = PegNakedSuffix(primary)

    /**
     * A shortcut creating [PegLiteralPrimary]
     */
    operator fun String.unaryPlus(): PegPrimary = PegLiteralPrimary(this)

    /**
     * A shortcut creating [PegClassPrimary]
     */
    fun cls(init: PegClassPrimary.Builder.() -> Unit): PegClassPrimary {
        val builder = PegClassPrimary.Builder()
        builder.init()
        return builder.build()
    }

    /**
     * A shortcut creating [PegGroupPrimary]
     */
    fun g(exp: PegExpression): PegGroupPrimary = PegGroupPrimary(exp)

    /**
     * A shortcut to detect dot
     */
    val dot = PegDotPrimary
}