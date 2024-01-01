package io.github.derui.pegen.core.parser

/**
 * A type of parsing result.
 *
 * This type provides some utility functions to unwrap the value.
 *
 * ```
 * val result = ParsingResult.constructedAs(Hoge())
 *
 * ParsingResult { result.asType<Hoge>() } // GetHoge()
 * ParsingResult { result.asString() } // null
 * ```
 */
sealed class ParsingResult<T> private constructor(
    val restSource: ParserSource,
) {
    companion object {
        /**
         * Create a [Raw] instance.
         */
        fun <R> rawOf(
            value: String,
            rest: ParserSource,
        ): ParsingResult<R> = Raw(value, rest)

        /**
         * Create a [Constructed] instance.
         */
        fun <R> constructedAs(
            value: R,
            rest: ParserSource,
        ): ParsingResult<R> = Constructed(value, rest)

        /**
         * Provides a simple DSL to unwrap the value.
         */
        inline operator fun <reified R> invoke(f: Companion.() -> R): R = this.f()

        /**
         * A simple unwrap function to get type of [R].
         */
        inline fun <reified R> ParsingResult<*>.asType(): R? =
            when (this) {
                is Raw -> null
                is Constructed -> if (this.value is R) this.value else null
            }

        /**
         * A simple unwrap function to get string value.
         */
        fun ParsingResult<*>.asString(): String? =
            when (this) {
                is Raw -> this.value
                is Constructed -> null
            }

        /**
         * A simple unwrap function to get long value. This function only works for [Raw] value.
         */
        fun ParsingResult<*>.asLong(): Long? =
            when (this) {
                is Raw -> this.value.toLongOrNull()
                is Constructed -> null
            }
    }

    internal abstract fun replaceWith(rest: ParserSource): ParsingResult<T>

    /**
     * Raw value of parsing result.
     */
    class Raw<T> internal constructor(val value: String, rest: ParserSource) : ParsingResult<T>(rest) {
        override fun toString(): String = value

        override fun replaceWith(rest: ParserSource): ParsingResult<T> {
            return Raw(value, rest)
        }

        // / Generated by IntelliJ IDEA ///
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Raw<*>

            return value == other.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }

    /**
     * Constructed value of parsing result. This type is used by definition.
     */
    class Constructed<T> internal constructor(val value: T, rest: ParserSource) : ParsingResult<T>(rest) {
        override fun toString(): String = value.toString()

        override fun replaceWith(rest: ParserSource): ParsingResult<T> {
            return Constructed(value, rest)
        }

        // / Generated by IntelliJ IDEA ///
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Constructed<*>

            return value == other.value
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }
    }
}
