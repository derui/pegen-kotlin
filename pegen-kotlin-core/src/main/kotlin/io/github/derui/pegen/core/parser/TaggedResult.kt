package io.github.derui.pegen.core.parser

/**
 * [TaggedResult] is a utility type to get tagged result of parsing.
 *
 * This type is immutable. But provides some functions to get/change value.
 *
 * ```
 * val result = TaggedResult.new<String>()
 * result + ParsingResult.constructedAs("test") // add new result
 * result[0]?.asType() // "test"
 */
class TaggedResult<T> private constructor(
    private val results: List<ParsingResult<T>>,
) {
    companion object {
        internal fun <T> new(): TaggedResult<T> = TaggedResult(emptyList())
    }

    /**
     * Get tagged result as [List].
     */
    fun asList(): List<ParsingResult<T>> = results.toList()

    /**
     * A shortcut function to get first result as [R].
     */
    inline fun <reified R : T> firstAsType(): R? = get(0)?.let { it.value() as? R }

    /**
     * A shortcut function to get first read string.
     */
    fun firstRead(): String? = get(0)?.read

    /**
     * Get [ParsingResult] by index.
     */
    operator fun get(index: Int): ParsingResult<T>? = results.getOrNull(index)

    /**
     * Get new [TaggedResult] with new [ParsingResult] added.
     */
    internal operator fun plus(result: ParsingResult<T>): TaggedResult<T> = TaggedResult(results + result)
}
