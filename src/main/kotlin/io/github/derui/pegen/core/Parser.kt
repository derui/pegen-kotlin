package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.dsl.support.DefaultSyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegExpression

/**
 * A parser object that is used to DSL named PEG
 */
object Parser {
    operator fun <V, TagType> invoke(init: PegDsl<V, TagType>.() -> PegExpression<V, TagType>): PegExpression<V, TagType> {
        val generator = DefaultSyntaxIdentifierGenerator()
        return PegDsl<V, TagType>(generator).init()
    }
}
