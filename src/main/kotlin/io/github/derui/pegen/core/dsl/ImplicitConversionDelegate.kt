package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.dsl.support.SyntaxIdentifierGenerator
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
class ImplicitPegPrimary internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val primary: PegPrimary,
) : ImplicitConversionDelegate, PegPrimaryMarker {
    override fun asPrimary(): PegPrimary = primary

    override fun asSuffix(): PegSuffix = PegNakedSuffix(primary, generator.generate())

    override fun asPrefix(): PegPrefix = PegNakedPrefix(PegNakedSuffix(primary, generator.generate()), generator.generate())

    override fun asSequence(): PegSequence =
        PegSequence(
            listOf(
                PegNakedPrefix(
                    PegNakedSuffix(primary, generator.generate()),
                    generator.generate(),
                ),
            ),
            generator.generate(),
        )
}

/**
 * An implicit version of [PegSuffix]
 */
class ImplicitPegSuffix internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val suffix: PegSuffix,
) : ImplicitConversionDelegate, PegSuffixMarker {
    override fun asSuffix(): PegSuffix = suffix

    override fun asPrefix(): PegPrefix = PegNakedPrefix(suffix, generator.generate())

    override fun asSequence(): PegSequence = PegSequence(listOf(PegNakedPrefix(suffix, generator.generate())), generator.generate())
}

/**
 * An implicit version of [PegPrefix]
 */
class ImplicitPegPrefix internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val prefix: PegPrefix,
) : ImplicitConversionDelegate, PegPrefixMarker {
    override fun asPrefix(): PegPrefix = prefix

    override fun asSequence(): PegSequence = PegSequence(listOf(prefix), generator.generate())
}

/**
 * An implicit version of [PegSequence].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegSequence internal constructor(private val sequence: PegSequence) : ImplicitConversionDelegate, PegSequenceMarker {
    override fun asSequence(): PegSequence = sequence
}
