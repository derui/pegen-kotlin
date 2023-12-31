package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A base class for primary component of PEG
 */
sealed interface PegPrimary<T, TagType> : PegSyntax<T, TagType>

/**
 * This primary is a PEG's identifier
 */
class PegIdentifierPrimary<T, TagType> internal constructor(
    internal val identifier: PegDefinition<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrimary<T, TagType>

/**
 * This primary is a representation of PEG's literal
 */
class PegLiteralPrimary<T, TagType> internal constructor(
    internal val literal: String,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrimary<T, TagType>

/**
 * This primary is a PEG's [(p)] primary
 */
class PegClassPrimary<T, TagType> internal constructor(
    internal val cls: Set<Char>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrimary<T, TagType> {
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
        internal fun <T, TagType> build(tag: TagType?): PegClassPrimary<T, TagType> {
            require(chars.isNotEmpty()) {
                "Character class needs least 1 character"
            }

            return PegClassPrimary(chars, id, tag)
        }
    }

    override fun toString(): String {
        return "[${cls.joinToString()}]"
    }
}

/**
 * This primary is a PEG's [(e)] primary
 */
class PegGroupPrimary<T, TagType> internal constructor(
    internal val expression: PegExpression<T, TagType>,
    override val id: UUID,
    override val tag: TagType? = null,
) : PegPrimary<T, TagType>

/**
 * This primary is a PEG's [.] primary
 */
class PegDotPrimary<T, TagType> internal constructor(override val id: UUID, override val tag: TagType? = null) : PegPrimary<T, TagType>
