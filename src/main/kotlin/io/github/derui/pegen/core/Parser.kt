package io.github.derui.pegen.core

import io.github.derui.pegen.core.dsl.PegDsl
import io.github.derui.pegen.core.lang.PegExpression

/**
 * A parser object that is used to DSL named PEG
 */
object PEG {
    operator fun <T : Tag> invoke(init: PegDsl.() -> PegExpression): PegExpression {
        return PegDsl().init()
    }
}
