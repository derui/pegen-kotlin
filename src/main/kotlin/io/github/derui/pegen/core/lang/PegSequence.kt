package io.github.derui.pegen.core.lang

/**
 * A peg sequence is a sequence of peg prefix
 */
class PegSequence internal constructor(
    private val prefixes: List<PegPrefix>,
)
