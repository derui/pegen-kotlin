package io.github.derui.pegen.core.parsing

/**
 * A simple error info. This class is completely immutable.
 */
class ErrorInfo internal constructor(
    val messages: List<String>,
    val position: Position,
) {
    /**
     * Merge two error info. The position of the merged error info is the smaller one of the two.
     */
    fun merge(other: ErrorInfo): ErrorInfo {
        return ErrorInfo(messages + other.messages, if (position < other.position) position else other.position)
    }
}
