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
sealed interface ImplicitConversionDelegate<T> {
    fun asPrimary(): PegPrimary<T> = throw UnsupportedOperationException()

    fun asSuffix(): PegSuffix<T> = throw UnsupportedOperationException()

    fun asPrefix(): PegPrefix<T> = throw UnsupportedOperationException()

    fun asSequence(): PegSequence<T> = throw UnsupportedOperationException()
}

/**
 * An implicit version of [PegPrimary]
 */
class ImplicitPegPrimary<T> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val primary: PegPrimary<T>,
) : ImplicitConversionDelegate<T>, PegPrimaryMarker {
    override fun asPrimary() = primary

    override fun asSuffix() = PegNakedSuffix(primary, generator.generate())

    override fun asPrefix() = PegNakedPrefix<T>(PegNakedSuffix(primary, generator.generate()), generator.generate())

    override fun asSequence() =
        PegSequence<T>(
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
class ImplicitPegSuffix<T> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val suffix: PegSuffix<T>,
) : ImplicitConversionDelegate<T>, PegSuffixMarker {
    override fun asSuffix() = suffix

    override fun asPrefix() = PegNakedPrefix<T>(suffix, generator.generate())

    override fun asSequence() = PegSequence<T>(listOf(PegNakedPrefix(suffix, generator.generate())), generator.generate())
}

/**
 * An implicit version of [PegPrefix]
 */
class ImplicitPegPrefix<T> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val prefix: PegPrefix<T>,
) : ImplicitConversionDelegate<T>, PegPrefixMarker {
    override fun asPrefix() = prefix

    override fun asSequence() = PegSequence(listOf(prefix), generator.generate())
}

/**
 * An implicit version of [PegSequence].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegSequence<T> internal constructor(private val sequence: PegSequence<T>) : ImplicitConversionDelegate<T>, PegSequenceMarker {
    override fun asSequence() = sequence
}
