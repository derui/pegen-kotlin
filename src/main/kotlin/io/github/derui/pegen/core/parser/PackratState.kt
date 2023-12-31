package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegExpression
import java.util.UUID

/**
 * [PackratState] contains information and logics for parsing on PEG syntax [PegExpression] with packrat parsing.
 *
 * This class is NOT thread-safe.
 */
class PackratState<V, TagType> private constructor(
    private val cache: Array<MutableMap<UUID, ParserResultCache<V>>>,
    private val expressions: Map<UUID, PegDefinition<V, TagType>>,
) {
    companion object {
        fun <V, TagType> from(
            input: String,
            expressions: List<PegDefinition<V, TagType>>,
        ): PackratState<V, TagType> {
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
