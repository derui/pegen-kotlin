package io.github.derui.pegen.core.dsl

import io.github.derui.pegen.core.dsl.support.SyntaxIdentifierGenerator
import io.github.derui.pegen.core.lang.PegExpression
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
    fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> = throw UnsupportedOperationException()

    fun asPrimary(): PegPrimary<T, TagType> = throw UnsupportedOperationException()

    fun asSuffix(): PegSuffix<T, TagType> = throw UnsupportedOperationException()

    fun asPrefix(): PegPrefix<T, TagType> = throw UnsupportedOperationException()

    fun asSequence(): PegSequence<T, TagType> = throw UnsupportedOperationException()

    fun asExpression(): PegExpression<T, TagType> = throw UnsupportedOperationException()
}

/**
 * An implicit version of [PegPrimary]
 */
class ImplicitPegPrimary<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val primary: (TagType?) -> PegPrimary<T, TagType>,
    private var tag: TagType? = null,
) : ImplicitConversionDelegate<T, TagType> {
    override fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> {
        this.tag = tag

        return this
    }

    override fun asPrimary() = primary(tag)

    override fun asSuffix() = PegNakedSuffix(asPrimary(), generator.generate())

    override fun asPrefix() = PegNakedPrefix(asSuffix(), generator.generate())

    override fun asSequence() = PegSequence(listOf(asPrefix()), generator.generate())

    override fun asExpression(): PegExpression<T, TagType> = PegExpression(listOf(asSequence()), generator.generate())
}

/**
 * An implicit version of [PegSuffix]
 */
class ImplicitPegSuffix<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val suffix: (TagType?) -> PegSuffix<T, TagType>,
) : ImplicitConversionDelegate<T, TagType> {
    private var tag: TagType? = null

    override fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> {
        this.tag = tag

        return this
    }

    override fun asSuffix() = suffix(tag)

    override fun asPrefix() = PegNakedPrefix(suffix(tag), generator.generate())

    override fun asSequence() = PegSequence(listOf(PegNakedPrefix(suffix(tag), generator.generate())), generator.generate())

    override fun asExpression(): PegExpression<T, TagType> = PegExpression(listOf(asSequence()), generator.generate())
}

/**
 * An implicit version of [PegPrefix]
 */
class ImplicitPegPrefix<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val prefix: (TagType?) -> PegPrefix<T, TagType>,
) : ImplicitConversionDelegate<T, TagType> {
    private var tag: TagType? = null

    override fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> {
        this.tag = tag

        return this
    }

    override fun asPrefix() = prefix(tag)

    override fun asSequence() = PegSequence(listOf(prefix(tag)), generator.generate())

    override fun asExpression(): PegExpression<T, TagType> = PegExpression(listOf(asSequence()), generator.generate())
}

/**
 * An implicit version of [PegSequence].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegSequence<T, TagType> internal constructor(
    private val generator: SyntaxIdentifierGenerator,
    private val sequence: (TagType?) -> PegSequence<T, TagType>,
) : ImplicitConversionDelegate<T, TagType> {
    private var tag: TagType? = null

    override fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> {
        this.tag = tag

        return this
    }

    override fun asSequence() = sequence(tag)

    override fun asExpression(): PegExpression<T, TagType> = PegExpression(listOf(asSequence()), generator.generate())
}

/**
 * An implicit version of [PegExpression].
 *
 * This class is only defined for consistency of DSL.
 */
class ImplicitPegExpression<T, TagType> internal constructor(
    private val expression: (TagType?) -> PegExpression<T, TagType>,
) : ImplicitConversionDelegate<T, TagType> {
    private var tag: TagType? = null

    override fun tagged(tag: TagType): ImplicitConversionDelegate<T, TagType> {
        this.tag = tag

        return this
    }

    override fun asExpression(): PegExpression<T, TagType> = expression(tag)
}
