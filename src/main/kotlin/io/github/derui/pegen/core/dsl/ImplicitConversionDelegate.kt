package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegPrefix
import io.github.derui.pegen.core.lang.PegPrimary
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.lang.PegSuffix

/**
 * Default interface for implicit conversion in DSL.
 */
sealed interface ImplicitConversionDelegate {
    fun asPrimary(): PegPrimary = throw UnsupportedOperationException()
    fun asSuffix(): PegSuffix = throw UnsupportedOperationException()
    fun asPrefix(): PegPrefix = throw UnsupportedOperationException()
    fun asSequence(): PegSequence = throw UnsupportedOperationException()
}

/**
 * An implicit version of [PegPrimary]
 */
class ImplicitPegPrimary(private val primary: PegPrimary) : ImplicitConversionDelegate, PegPrimaryMarker {
    override fun asPrimary(): PegPrimary = primary
    override fun asSuffix(): PegSuffix = PegNakedSuffix(primary)
    override fun asPrefix(): PegPrefix = PegNakedPrefix(PegNakedSuffix(primary))
    override fun asSequence(): PegSequence = PegSequence(listOf(PegNakedPrefix(PegNakedSuffix(primary))))
}

/**
 * An implicit version of [PegSuffix]
 */
class ImplicitPegSuffix(private val suffix: PegSuffix) : ImplicitConversionDelegate, PegSuffixMarker {
    override fun asSuffix(): PegSuffix = suffix
    override fun asPrefix(): PegPrefix = PegNakedPrefix(suffix)
    override fun asSequence(): PegSequence = PegSequence(listOf(PegNakedPrefix(suffix)))
}

/**
 * An implicit version of [PegPrefix]
 */
class ImplicitPegPrefix(private val prefix: PegPrefix) : ImplicitConversionDelegate, PegPrefixMarker {
    override fun asPrefix(): PegPrefix = prefix
    override fun asSequence(): PegSequence = PegSequence(listOf(prefix))
}

/**
 * An implicit version of [PegSequence].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegSequence(private val sequence: PegSequence) : ImplicitConversionDelegate, PegSequenceMarker {
    override fun asSequence(): PegSequence = sequence
}
