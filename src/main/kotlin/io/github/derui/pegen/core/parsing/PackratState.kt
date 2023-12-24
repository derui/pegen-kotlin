package io.github.derui.pegen.core.parsing

import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegExpressionIntermediate
import java.util.UUID

/**
 * [PackratState] contains information and logics for parsing on PEG syntax [PegExpressionIntermediate] with packrat parsing.
 *
 * This class is NOT thread-safe.
 */
class PackratState<V> private constructor(
    private val cache: Array<MutableMap<UUID, ParsingResult<V>>>,
    private val expressions: Map<UUID, PegExpression<V>>,
) {
    companion object {
        fun <V> from(
            input: String,
            expressions: List<PegExpression<V>>,
        ): PackratState<V> {
            val expMap = expressions.associateBy { it.id }

            return PackratState(Array(input.length) { expMap.mapValues { NoParse<V>() }.toMutableMap() }, expMap)
        }
    }
}
