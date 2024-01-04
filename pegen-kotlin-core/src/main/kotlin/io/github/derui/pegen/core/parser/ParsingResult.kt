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
sealed class ParsingResult<T> {
    /**
     * Rest of parse
     */
    abstract val restSource: ParserSource

    /**
     * The string read from a parser
     */
    abstract val read: String

    companion object {
        /**
         * Create a [Raw] instance.
         */
        fun <R> from(
            value: R,
            read: String,
            rest: ParserSource,
        ): ParsingResult<R> = Raw(read, value, rest)

        /**
         * Create an instance to mark it is empty
         */
        fun <R> noValueOf(
            read: String,
            rest: ParserSource,
        ): ParsingResult<R> = NoValue(read, rest)
    }

    /**
     * A simple unwrap function to get type of [T].
     */
    abstract fun value(): T

    /**
     * Raw value of parsing result.
     */
    private data class Raw<T>(override val read: String, val value: T, override val restSource: ParserSource) : ParsingResult<T>() {
        override fun value(): T = value
    }

    private data class NoValue<T>(override val read: String, override val restSource: ParserSource) : ParsingResult<T>() {
        override fun value(): T = throw NotImplementedError("Empty result can not get value")
    }
}
