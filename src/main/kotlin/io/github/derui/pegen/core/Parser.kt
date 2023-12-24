package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.dsl.support.DefaultSyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegExpressionIntermediate

/**
 * A parser object that is used to DSL named PEG
 */
object PEG {
    operator fun <V, TagType : Tag> invoke(
        init: PegDsl<V, TagType>.() -> PegExpressionIntermediate<V, TagType>,
    ): PegExpressionIntermediate<V, TagType> {
        val generator = DefaultSyntaxIdentifierGenerator()
        return PegDsl<V, TagType>(generator).init()
    }
}
