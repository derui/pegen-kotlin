package io.github.derui.pegen.core.parser

/**
 * A simple error info. This class is completely immutable.
 */
class ErrorInfo private constructor(
    val messages: List<String>,
    val position: Position,
) {
    companion object {
        /**
         * Create new [ErrorInfo] from message and position
         */
        internal fun from(
            message: String,
            position: Position,
        ) = ErrorInfo(listOf(message), position)
    }

    override fun toString(): String = "ErrorInfo(messages=$messages, position=$position)"

    // / Generated by IntelliJ IDEA ///
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ErrorInfo

        if (messages != other.messages) return false
        if (position != other.position) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messages.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}
