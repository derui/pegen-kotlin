package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.*

/**
 * Dsl for PEG
 *
 */
class PegDsl<T : Tag> internal constructor() {

    /**
     * Create a new [PegExpression] with the given [sequences]. This function implicitly expresses CHOICE.
     */
    fun <T> exp(vararg sequences: T): PegExpression
        where T : PegSequenceMarker, T : ImplicitConversionDelegates = PegExpression(sequences.map { it.asSequence() }.toList())

    /**
     * Create a new [PegSequence] without any prefix. This function implicitly expresses SEQUENCE.
     */
    fun s(): ImplicitPegSequence = ImplicitPegSequence(PegSequence(emptyList()))

    /**
     * Create a new [PegSequence] with the given [prefixes]. This function implicitly expresses SEQUENCE.
     */
    fun <T> s(vararg prefixes: T): ImplicitPegSequence
        where T : PegPrefixMarker, T : ImplicitConversionDelegates = ImplicitPegSequence(PegSequence(prefixes.map { it.asPrefix() }.toList()))

    /**
     *  Create a new [PegAndPrefix] with the given [suffix].
     */
    fun <T> and(suffix: T): ImplicitPegPrefix
        where T : PegSuffixMarker, T : ImplicitConversionDelegates = ImplicitPegPrefix(PegAndPrefix(suffix.asSuffix()))

    /**
     *  Create a new [PegNotPrefix] with the given [suffix].
     */
    fun <T> not(suffix: T): ImplicitPegPrefix
        where T : PegSuffixMarker, T : ImplicitConversionDelegates = ImplicitPegPrefix(PegNotPrefix(suffix.asSuffix()))

    /**
     * Create a new [PegNakedPrefix] with the given [suffix].
     */
    fun <T> np(suffix: T): ImplicitPegPrefix
        where T : PegSuffixMarker, T : ImplicitConversionDelegates = ImplicitPegPrefix(PegNakedPrefix(suffix.asSuffix()))

    /**
     * Create a new [PegSuffix] as a representation of [*] in PEG
     */
    fun <T> many(primary: PegPrimary): ImplicitPegSuffix
        where T : PegPrimaryMarker, T : ImplicitConversionDelegates = ImplicitPegSuffix(PegStarSuffix(primary))

    /**
     * Create a new [PegSuffix] as a representation of [+] in PEG
     */
    fun <T> many1(primary: PegPrimary): ImplicitPegSuffix
        where T : PegPrimaryMarker, T : ImplicitConversionDelegates = ImplicitPegSuffix(PegPlusSuffix(primary))

    /**
     * Create a new [PegSuffix] as a representation of [?] in PEG
     */
    fun <T> opt(primary: PegPrimary): ImplicitPegSuffix
        where T : PegPrimaryMarker, T : ImplicitConversionDelegates = ImplicitPegSuffix(PegQuestionSuffix(primary))

    /**
     * Create a new [PegSuffix] without any suffix
     */
    fun <T> ns(primary: PegPrimary): ImplicitPegSuffix
        where T : PegPrimaryMarker, T : ImplicitConversionDelegates = ImplicitPegSuffix(PegNakedSuffix(primary))

    /**
     * A shortcut creating [PegLiteralPrimary]
     */
    operator fun String.unaryPlus(): ImplicitPegPrimary = ImplicitPegPrimary(PegLiteralPrimary(this))

    /**
     * A shortcut creating [PegClassPrimary]
     */
    fun cls(init: PegClassPrimary.Builder.() -> Unit): ImplicitPegPrimary {
        val builder = PegClassPrimary.Builder()
        builder.init()
        return ImplicitPegPrimary(builder.build())
    }

    /**
     * A shortcut creating [PegGroupPrimary]
     */
    fun g(exp: PegExpression): ImplicitPegPrimary = ImplicitPegPrimary(PegGroupPrimary(exp))

    /**
     * A shortcut to detect dot
     */
    val dot = ImplicitPegPrimary(PegDotPrimary)
}