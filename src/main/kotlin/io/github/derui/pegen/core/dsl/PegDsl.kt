package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.dsl.support.SyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegGroupPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegNotPrefix
import io.github.derui.pegen.core.lang.PegPlusSuffix
import io.github.derui.pegen.core.lang.PegPrimary
import io.github.derui.pegen.core.lang.PegQuestionSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.lang.PegStarSuffix

/**
 * Dsl for PEG
 *
 */
class PegDsl<V> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
) {
    /**
     * Create a new [PegExpression] with the given [sequences]. This function implicitly expresses CHOICE.
     */
    fun <T> exp(vararg sequences: T): PegExpression<T> where T : PegSequenceMarker, T : ImplicitConversionDelegate<V> =
        PegExpression(
            generator.generate(),
            sequences.map {
                it.asSequence()
            }.toList(),
        )

    /**
     * Create a new [PegSequence] without any prefix. This function implicitly expresses SEQUENCE.
     */
    fun s(): ImplicitPegSequence<V> = ImplicitPegSequence(PegSequence(emptyList(), generator.generate()))

    /**
     * Create a new [PegSequence] with the given [prefixes]. This function implicitly expresses SEQUENCE.
     */
    fun <T> s(vararg prefixes: T): ImplicitPegSequence<V> where T : PegPrefixMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegSequence(
            PegSequence(prefixes.map { it.asPrefix() }.toList(), generator.generate()),
        )

    /**
     *  Create a new [PegAndPrefix] with the given [suffix].
     */
    fun <T> and(suffix: T): ImplicitPegPrefix<V> where T : PegSuffixMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegPrefix(
            generator,
            PegAndPrefix(suffix.asSuffix(), generator.generate()),
        )

    /**
     *  Create a new [PegNotPrefix] with the given [suffix].
     */
    fun <T> not(suffix: T): ImplicitPegPrefix<V> where T : PegSuffixMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegPrefix(
            generator,
            PegNotPrefix(suffix.asSuffix(), generator.generate()),
        )

    /**
     * Create a new [PegNakedPrefix] with the given [suffix].
     */
    fun <T> np(suffix: T): ImplicitPegPrefix<V> where T : PegSuffixMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegPrefix(
            generator,
            PegNakedPrefix(suffix.asSuffix(), generator.generate()),
        )

    /**
     * Create a new [PegSuffix] as a representation of [*] in PEG
     */
    fun <T> many(primary: PegPrimary<V>): ImplicitPegSuffix<V> where T : PegPrimaryMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegSuffix(
            generator,
            PegStarSuffix(primary, generator.generate()),
        )

    /**
     * Create a new [PegSuffix] as a representation of [+] in PEG
     */
    fun <T> many1(primary: PegPrimary<V>): ImplicitPegSuffix<V> where T : PegPrimaryMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegSuffix(
            generator,
            PegPlusSuffix(primary, generator.generate()),
        )

    /**
     * Create a new [PegSuffix] as a representation of [?] in PEG
     */
    fun <T> opt(primary: PegPrimary<V>): ImplicitPegSuffix<V> where T : PegPrimaryMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegSuffix(
            generator,
            PegQuestionSuffix(primary, generator.generate()),
        )

    /**
     * Create a new [PegSuffix] without any suffix
     */
    fun <T> ns(primary: PegPrimary<V>): ImplicitPegSuffix<V> where T : PegPrimaryMarker, T : ImplicitConversionDelegate<V> =
        ImplicitPegSuffix(
            generator,
            PegNakedSuffix(primary, generator.generate()),
        )

    /**
     * A shortcut creating [PegLiteralPrimary]
     */
    operator fun String.unaryPlus(): ImplicitPegPrimary<V> = ImplicitPegPrimary(generator, PegLiteralPrimary(this, generator.generate()))

    /**
     * A shortcut creating [PegClassPrimary]
     */
    fun cls(init: PegClassPrimary.Builder.() -> Unit): ImplicitPegPrimary<V> {
        val builder = PegClassPrimary.Builder(generator.generate())
        builder.init()
        return ImplicitPegPrimary(generator, builder.build())
    }

    /**
     * A shortcut creating [PegGroupPrimary]
     */
    fun g(exp: PegExpression<V>): ImplicitPegPrimary<V> = ImplicitPegPrimary(generator, PegGroupPrimary(exp, generator.generate()))

    /**
     * A shortcut to detect dot
     */
    val dot = ImplicitPegPrimary<V>(generator, PegDotPrimary(generator.generate()))
}
