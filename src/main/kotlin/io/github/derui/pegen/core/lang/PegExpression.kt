package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A class for PEG expression
 */
class PegExpression internal constructor(
    override val id: UUID,
    private val sequeces: List<PegSequence>,
) : PegSyntax
