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
sealed interface ImplicitConversionDelegate<T, TagType> {
    fun asPrimary(): PegPrimary<T, TagType> = throw UnsupportedOperationException()

    fun asSuffix(): PegSuffix<T, TagType> = throw UnsupportedOperationException()

    fun asPrefix(): PegPrefix<T, TagType> = throw UnsupportedOperationException()

    fun asSequence(): PegSequence<T, TagType> = throw UnsupportedOperationException()
}

/**
 * An implicit version of [PegPrimary]
 */
class ImplicitPegPrimary<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val primary: PegPrimary<T, TagType>,
) : ImplicitConversionDelegate<T, TagType>, PegPrimaryMarker {
    override fun asPrimary() = primary

    override fun asSuffix() = PegNakedSuffix(primary, generator.generate())

    override fun asPrefix() = PegNakedPrefix(PegNakedSuffix(primary, generator.generate()), generator.generate())

    override fun asSequence() =
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
class ImplicitPegSuffix<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val suffix: PegSuffix<T, TagType>,
) : ImplicitConversionDelegate<T, TagType>, PegSuffixMarker {
    override fun asSuffix() = suffix

    override fun asPrefix() = PegNakedPrefix(suffix, generator.generate())

    override fun asSequence() = PegSequence(listOf(PegNakedPrefix(suffix, generator.generate())), generator.generate())
}

/**
 * An implicit version of [PegPrefix]
 */
class ImplicitPegPrefix<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val prefix: PegPrefix<T, TagType>,
) : ImplicitConversionDelegate<T, TagType>, PegPrefixMarker {
    override fun asPrefix() = prefix

    override fun asSequence() = PegSequence(listOf(prefix), generator.generate())
}

/**
 * An implicit version of [PegSequence].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegSequence<T, TagType> internal constructor(
    private val sequence: PegSequence<T, TagType>,
) : ImplicitConversionDelegate<T, TagType>, PegSequenceMarker {
    override fun asSequence() = sequence
}
