package io.github.derui.pegen.core.dsl.support

import java.util.UUID

/**
 * A default implementation of [SyntaxIdentifierGenerator]
 */
class DefaultSyntaxIdentifierGenerator : SyntaxIdentifierGenerator {
    override fun generate(): UUID = UUID.randomUUID()
}
