package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A base class for primary component of PEG
 */
sealed interface PegPrimary<T> : PegSyntax<T>

/**
 * This primary is a PEG's identifier
 */
class PegIdentifierPrimary<T> internal constructor(
    // TODO We want to treat Identifier as class
    private val identifier: PegExpression<T>,
    override val id: UUID,
) : PegPrimary<T>

/**
 * This primary is a representation of PEG's literal
 */
class PegLiteralPrimary<T> internal constructor(
    private val literal: String,
    override val id: UUID,
) : PegPrimary<T>

/**
 * This primary is a PEG's [(p)] primary
 */
class PegClassPrimary<T> internal constructor(
    private val cls: Set<Char>,
    override val id: UUID,
) : PegPrimary<T> {
    /**
     * A simple builder for [PegClassPrimary]
     */
    class Builder internal constructor(private val id: UUID) {
        private val chars = mutableSetOf<Char>()

        /**
         * Add string to char class
         */
        operator fun String.unaryPlus() {
            chars.addAll(this.toSet())
        }

        /**
         * Add char range to char class
         */
        operator fun CharRange.unaryPlus() {
            chars.addAll(this.toSet())
        }

        /**
         * Add char to char class
         */
        operator fun Char.unaryPlus() {
            chars.add(this)
        }

        /**
         * Build [PegClassPrimary] from this builder
         */
        fun <T> build(): PegClassPrimary<T> = PegClassPrimary<T>(chars, id)
    }
}

/**
 * This primary is a PEG's [(e)] primary
 */
class PegGroupPrimary<T> internal constructor(
    private val expression: PegExpression<T>,
    override val id: UUID,
) : PegPrimary<T>

/**
 * This primary is a PEG's [.] primary
 */
class PegDotPrimary<T> internal constructor(override val id: UUID) : PegPrimary<T>
