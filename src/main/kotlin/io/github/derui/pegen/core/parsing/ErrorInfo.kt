package io.github.derui.pegen.core.parsing

/**
 * A simple error info. This class is completely immutable.
 */
class ErrorInfo internal constructor(
    val message: String,
    val position: Position,
)
