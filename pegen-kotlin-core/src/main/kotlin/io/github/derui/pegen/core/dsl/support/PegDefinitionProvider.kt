package io.github.derui.pegen.core.dsl.support

import io.github.derui.pegen.core.lang.PegDefinition

/**
 * A provider for [PegDefinition]. This is used with companion object pattern.
 *
 * ```
 * class Hoge {
 *    companion object: Pegen {
 *      +"s"
 *    } constructAt {
 *      Hoge()
 *    }
 * }
 * ```
 */
fun interface PegDefinitionProvider<T, TagType> {
    /**
     * Provide a [PegDefinition].
     */
    fun provide(): PegDefinition<T, TagType>
}
