package io.github.derui.pegen.core.parsing

/**
 * [Position] holds and represents a position in a source code.
 */
class Position private constructor(
    val line: Int,
    val column: Int,
) {
    companion object {
        /**
         * Start position of a source code.
         */
        fun start() = Position(1, 1)
    }

    /**
     * Move forward a position with [c].
     */
    fun forward(c: Char) = when (c) {
        '\n' -> Position(line + 1, 1)
        else -> Position(line, column + 1)
    }

    override fun toString(): String {
        return "($line, $column)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (line != other.line) return false
        if (column != other.column) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + column
        return result
    }
}
