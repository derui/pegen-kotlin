package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegExpression
import java.util.UUID

/**
 * [PackratState] contains information and logics for parsing on PEG syntax [PegExpression] with packrat parsing.
 *
 * This class is NOT thread-safe.
 */
class PackratState<V> private constructor(
    private val cache: Array<MutableMap<UUID, ParserResultCache<V>>>,
    private val expressions: Map<UUID, PegDefinition<V>>,
) {
    companion object {
        fun <V> from(
            input: String,
            expressions: List<PegDefinition<V>>,
        ): PackratState<V> {
            val expMap = expressions.associateBy { it.id }

            return PackratState(
                Array(input.length) {
                    expMap.mapValues {
                        ParserResultCache.NoParse<V>()
                    }.toMutableMap()
                },
                expMap,
            )
        }
    }
}
