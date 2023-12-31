package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import io.github.derui.pegen.core.support.Result
import io.github.derui.pegen.core.support.map

/**
 * A source of characters for parsing.
 */
class ParserSource private constructor(
    private var currentPosition: Position,
    private val input: String,
    private val startIndex: Int,
) {
    companion object {
        fun newWith(input: String) = ParserSource(Position.start(), input, 0)
    }

    /**
     * current index of this context
     */
    private var currentIndex: Int = startIndex

    /**
     * Get the current position of the cursor.
     */
    val position get() = currentPosition

    /**
     * Get the next character and move the cursor forward.
     * If index is reached to the end of input, return [Err] with [ErrorInfo].
     */
    fun advanceWhile(f: (Char) -> Boolean): Pair<String, ParserSource> {
        while (input.length > currentIndex && f(input[currentIndex])) {
            val c = input[currentIndex++]
            currentPosition = currentPosition.forward(c)
        }

        return parsed() to rest()
    }

    /**
     * Get the next character and do not move the cursor forward.
     * If index is reached to the end of input, return [Err] with [ErrorInfo].
     */
    fun peekChar(): Result<Char, ErrorInfo> {
        if (input.length <= currentIndex) {
            return Err(ErrorInfo.from("Unexpected end of input", currentPosition))
        }
        val c = input[currentIndex]

        return Ok(c)
    }

    /**
     * Get the next character and do not move the cursor forward.
     * If index is reached to the end of input, return [Err] with [ErrorInfo].
     */
    fun readChar(): Result<Pair<Char, ParserSource>, ErrorInfo> {
        return peekChar().map {
            it to ParserSource(currentPosition.forward(it), input, currentIndex + 1)
        }
    }

    /**
     * Get parsed string from start index to current index.
     */
    private fun parsed(): String = input.substring(startIndex, currentIndex)

    /**
     * Get rest of the input.
     */
    private fun rest() = ParserSource(currentPosition, input, currentIndex)

    /**
     * Get the entire string between [this] and [other].
     */
    operator fun rangeTo(other: ParserSource) = input.substring(startIndex, other.startIndex)

    /**
     * A shortcut function to create error info from current context
     */
    fun errorOf(message: String) = ErrorInfo.from(message, this.position)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParserSource

        if (currentPosition != other.currentPosition) return false
        if (input != other.input) return false
        if (startIndex != other.startIndex) return false
        if (currentIndex != other.currentIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentPosition.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + startIndex
        result = 31 * result + currentIndex
        return result
    }
}
