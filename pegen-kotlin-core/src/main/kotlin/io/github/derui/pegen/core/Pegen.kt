package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.ImplicitConversionDelegate
import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.dsl.support.DefaultSyntaxIdentifierGenerator
import io.github.derui.pegen.core.dsl.support.PegDefinitionProvider
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegExpression

/**
 * A parser generator with PEG DSL
 */
class Pegen<V, TagType> {
    /**
     * Make expression with DSL
     */
    infix fun define(init: PegDsl<V, TagType>.() -> ImplicitConversionDelegate<V, TagType>): PegExpression<V, TagType> {
        val generator = DefaultSyntaxIdentifierGenerator()

        val expr = PegDsl<V, TagType>(generator).init()

        return expr.asExpression()
    }

    /**
     * Make definition with DSL
     */
    abstract class Def<V, TagType>(
        private val provider: Pegen<V, TagType>.() -> PegDefinitionProvider<V, TagType>,
    ) : PegDefinitionProvider<V, TagType> {
        private val laziedProvider by lazy {
            val pegen = Pegen<V, TagType>()
            pegen.provider()
        }

        override fun provide(): PegDefinition<V, TagType> {
            return laziedProvider.provide()
        }
    }
}
