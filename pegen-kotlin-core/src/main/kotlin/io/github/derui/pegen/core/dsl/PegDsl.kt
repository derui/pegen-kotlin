package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.dsl.support.SyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegGroupPrimary
import io.github.derui.pegen.core.lang.PegIdentifierPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegNotPrefix
import io.github.derui.pegen.core.lang.PegPlusSuffix
import io.github.derui.pegen.core.lang.PegQuestionSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.lang.PegStarSuffix

/**
 * Dsl for PEG
 *
 */
class PegDsl<V, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
) {
    /**
     * Set [tag] to the given [ImplicitConversionDelegate] for constructing from expression.
     */
    infix fun ImplicitConversionDelegate<V, TagType>.tagged(tag: TagType): ImplicitConversionDelegate<V, TagType> = tagged(tag)

    /**
     * Create a new [PegExpression] with the given [sequences]. This function implicitly expresses CHOICE.
     */
    fun <T> exp(vararg sequences: T): ImplicitPegExpression<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegExpression {
            PegExpression(
                sequences.map {
                    it.asSequence()
                }.toList(),
                generator.generate(),
                it,
            )
        }

    /**
     * shortcut function to make expression
     */
    operator fun ImplicitConversionDelegate<V, TagType>.div(
        other: ImplicitConversionDelegate<V, TagType>,
    ): ImplicitConversionDelegate<V, TagType> {
        return ImplicitPegExpression {
            val expr1 = this.asExpression()
            val expr2 = other.asExpression()

            PegExpression(
                expr1.sequences + expr2.sequences,
                generator.generate(),
                it,
            )
        }
    }

    /**
     * Create a new [PegSequence] without any prefix. This function implicitly expresses SEQUENCE.
     */
    fun s(): ImplicitPegSequence<V, TagType> = ImplicitPegSequence(generator) { PegSequence(emptyList(), generator.generate(), it) }

    /**
     * Create a new [PegSequence] with the given [prefixes]. This function implicitly expresses SEQUENCE.
     */
    fun <T> s(vararg prefixes: T): ImplicitPegSequence<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegSequence(generator) { tag ->
            PegSequence(prefixes.map { it.asPrefix() }.toList(), generator.generate(), tag)
        }

    /**
     *  Create a new [PegAndPrefix] with the given [suffix].
     */
    fun <T> and(suffix: T): ImplicitPegPrefix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegPrefix(generator) { PegAndPrefix(suffix.asSuffix(), generator.generate(), it) }

    /**
     *  Create a new [PegNotPrefix] with the given [suffix].
     */
    fun <T> not(suffix: T): ImplicitPegPrefix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegPrefix(generator) { PegNotPrefix(suffix.asSuffix(), generator.generate(), it) }

    /**
     * Create a new [PegNakedPrefix] with the given [suffix].
     */
    fun <T> np(suffix: T): ImplicitPegPrefix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegPrefix(generator) { PegNakedPrefix(suffix.asSuffix(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [*] in PEG
     */
    fun <T> many(primary: T): ImplicitPegSuffix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegSuffix(generator) { PegStarSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [+] in PEG
     */
    fun <T> many1(primary: T): ImplicitPegSuffix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegSuffix(generator) { PegPlusSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [?] in PEG
     */
    fun <T> opt(primary: T): ImplicitPegSuffix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegSuffix(generator) { PegQuestionSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] without any suffix
     */
    fun <T> ns(primary: T): ImplicitPegSuffix<V, TagType> where T : ImplicitConversionDelegate<V, TagType> =
        ImplicitPegSuffix(
            generator,
        ) { PegNakedSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * A shortcut creating [PegLiteralPrimary]
     */
    operator fun String.unaryPlus(): ImplicitPegPrimary<V, TagType> =
        ImplicitPegPrimary(
            generator,
            { PegLiteralPrimary(this, generator.generate(), it) },
        )

    /**
     * A shortcut creating [PegClassPrimary]
     */
    fun cls(init: PegClassPrimary.Builder.() -> Unit): ImplicitPegPrimary<V, TagType> {
        return ImplicitPegPrimary(generator, {
            val builder = PegClassPrimary.Builder(generator.generate())
            builder.init()
            builder.build(it)
        })
    }

    /**
     * A shortcut creating [PegGroupPrimary]
     */
    fun g(exp: ImplicitConversionDelegate<V, TagType>): ImplicitPegPrimary<V, TagType> =
        ImplicitPegPrimary(generator, { PegGroupPrimary(exp.asExpression(), generator.generate(), it) })

    /**
     * A shortcut to detect dot
     */
    val dot = ImplicitPegPrimary<V, TagType>(generator, { PegDotPrimary(generator.generate(), it) })

    /**
     * A shortcut to create identifier
     */
    fun ident(v: PegDefinition<V, TagType>) =
        ImplicitPegPrimary<V, TagType>(generator, { PegIdentifierPrimary(v, generator.generate(), it) })
}
