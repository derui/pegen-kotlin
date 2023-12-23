package io.github.derui.pegen.core.lang

/**
 * A base class for primary component of PEG
 */
sealed interface PegPrimary

/**
 * This primary is a PEG's identifier
 */
class PegIdentifierPrimary internal constructor(
    // TODO We want to treat Identifier as class
    private val identifier: PegExpression
) : PegPrimary {}

/**
 * This primary is a representation of PEG's literal
 */
class PegLiteralPrimary internal constructor(
    private val literal: String
) : PegPrimary {}

/**
 * This primary is a PEG's [(p)] primary
 */
class PegClassPrimary internal constructor(
    private val cls: Set<Char>
) : PegPrimary {


    /**
     * A simple builder for [PegClassPrimary]
     */
    class Builder internal constructor() {
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
        fun build(): PegClassPrimary = PegClassPrimary(chars)
    }
}

/**
 * This primary is a PEG's [(e)] primary
 */
class PegGroupPrimary internal constructor(
    private val expression: PegExpression
) : PegPrimary {}

/**
 * This primary is a PEG's [.] primary
 */
object PegDotPrimary : PegPrimary {}