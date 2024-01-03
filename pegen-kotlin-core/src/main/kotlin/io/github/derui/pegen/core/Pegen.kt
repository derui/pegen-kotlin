package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.ImplicitConversionDelegate
import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.dsl.support.DefaultSyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegExpression

/**
 * A parser generator with PEG DSL
 */
class Pegen {
    /**
     * Make expression with DSL
     */
    operator fun <V, TagType> invoke(init: PegDsl<V, TagType>.() -> ImplicitConversionDelegate<V, TagType>): PegExpression<V, TagType> {
        val generator = DefaultSyntaxIdentifierGenerator()

        val expr = PegDsl<V, TagType>(generator).init()

        return expr.asExpression()
    }
}
