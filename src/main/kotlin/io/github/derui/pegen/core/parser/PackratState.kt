package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegExpression
import java.util.UUID

/**
 * [PackratState] contains information and logics for parsing on PEG syntax [PegExpression] with packrat parsing.
 *
 * This class is NOT thread-safe.
 */
class PackratState<V> private constructor(
    private val cache: MutableMap<StatePointer, ParserResultCache<ParsingResult<V>>>,
) {
    private data class StatePointer(
        val syntaxId: UUID,
        val index: Int,
    )

    companion object {
        fun <V> from(): PackratState<V> = PackratState(mutableMapOf())
    }

    /**
     * get cache for [syntaxId] at [index]
     */
    fun get(
        syntaxId: UUID,
        index: Int,
    ): ParserResultCache<ParsingResult<V>> {
        return cache.computeIfAbsent(StatePointer(syntaxId, index)) {
            ParserResultCache.noParse()
        }
    }

    /**
     * put parsing result with parsed result
     */
    fun putParsed(
        syntaxId: UUID,
        index: Int,
        result: ParsingResult<V>,
    ) {
        cache[StatePointer(syntaxId, index)] = ParserResultCache.parsed(result)
    }

    /**
     * put parsing result with error
     */
    fun putError(
        syntaxId: UUID,
        index: Int,
        error: ErrorInfo,
    ) {
        cache[StatePointer(syntaxId, index)] = ParserResultCache.parsed(error)
    }
}
