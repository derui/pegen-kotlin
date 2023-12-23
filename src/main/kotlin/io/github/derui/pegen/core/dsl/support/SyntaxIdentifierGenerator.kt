package io.github.derui.pegen.core.dsl.support

import java.util.UUID

/**
 * An interface for generating syntax identifier
 */
interface SyntaxIdentifierGenerator {
    fun generate(): UUID
}
