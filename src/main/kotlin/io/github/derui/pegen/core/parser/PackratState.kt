package io.github.derui.pegen.core.parser

import io.github.derui.pegen.core.lang.PegExpression
import java.util.UUID

/**
 * [PackratState] contains information and logics for parsing on PEG syntax [PegExpression] with packrat parsing.
 *
 * This class is NOT thread-safe.
 */
class PackratState<V> private constructor(
    private val cache: Array<MutableMap<UUID, ParserResultCache<ParsingResult<V>>>>,
) {
    companion object {
        fun <V> from(input: String): PackratState<V> {
            return PackratState(
                Array(input.length) { mutableMapOf() },
            )
        }
    }

    /**
     * get cache for [syntaxId] at [index]
     */
    fun get(
        syntaxId: UUID,
        index: Int,
    ): ParserResultCache<ParsingResult<V>> {
        return cache[index][syntaxId] ?: ParserResultCache.NoParse()
    }

    /**
     * put parsing result with parsed result
     */
    fun putParsed(
        syntaxId: UUID,
        index: Int,
        result: ParsingResult<V>,
    ) {
        cache[index][syntaxId] = ParserResultCache.parsed(result)
    }

    /**
     * put parsing result with error
     */
    fun putError(
        syntaxId: UUID,
        index: Int,
        error: ErrorInfo,
    ) {
        cache[index][syntaxId] = ParserResultCache.parsed(error)
    }
}
