package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A peg sequence is a sequence of peg prefix
 */
class PegSequence internal constructor(
    private val prefixes: List<PegPrefix>,
    override val id: UUID,
) : PegSyntax
