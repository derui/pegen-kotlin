package io.github.derui.pegen.core.lang

import java.util.UUID

/**
 * A sealed interface for PEG syntax
 */
sealed interface PegSyntax {
    val id: UUID
}
