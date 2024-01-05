package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.dsl.support.PegDefinitionProvider
import io.github.derui.pegen.core.dsl.support.SyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegGroupPrimary
import io.github.derui.pegen.core.lang.PegIdentifierPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
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
    inline infix fun <reified T : ImplicitConversionDelegate<V, TagType>> T.tagged(tag: TagType): T = tagged(tag) as T

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
    fun and(suffix: ImplicitPegSuffix<V, TagType>): ImplicitPegPrefix<V, TagType> =
        ImplicitPegPrefix(generator) { PegAndPrefix(suffix.asSuffix(), generator.generate(), it) }

    fun and(suffix: ImplicitPegPrimary<V, TagType>): ImplicitPegPrefix<V, TagType> =
        ImplicitPegPrefix(generator) { PegAndPrefix(suffix.asSuffix(), generator.generate(), it) }

    /**
     *  Create a new [PegNotPrefix] with the given [suffix].
     */
    fun not(suffix: ImplicitPegSuffix<V, TagType>): ImplicitPegPrefix<V, TagType> =
        ImplicitPegPrefix(generator) { PegNotPrefix(suffix.asSuffix(), generator.generate(), it) }

    fun not(suffix: ImplicitPegPrimary<V, TagType>): ImplicitPegPrefix<V, TagType> =
        ImplicitPegPrefix(generator) { PegNotPrefix(suffix.asSuffix(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [*] in PEG
     */
    fun many(primary: ImplicitPegPrimary<V, TagType>): ImplicitPegSuffix<V, TagType> =
        ImplicitPegSuffix(generator) { PegStarSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [+] in PEG
     */
    fun many1(primary: ImplicitPegPrimary<V, TagType>): ImplicitPegSuffix<V, TagType> =
        ImplicitPegSuffix(generator) { PegPlusSuffix(primary.asPrimary(), generator.generate(), it) }

    /**
     * Create a new [PegSuffix] as a representation of [?] in PEG
     */
    fun opt(primary: ImplicitPegPrimary<V, TagType>): ImplicitPegSuffix<V, TagType> =
        ImplicitPegSuffix(generator) { PegQuestionSuffix(primary.asPrimary(), generator.generate(), it) }

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
    operator fun PegDefinitionProvider<V, TagType>.invoke() =
        ImplicitPegPrimary<V, TagType>(generator, { PegIdentifierPrimary(this, generator.generate(), it) })
}
