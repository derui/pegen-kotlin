package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Test
import java.util.UUID

class PegExpressionRunnerTest {
    private enum class TagType

    @Test
    fun `parse expression`() {
        // Arrange
        val context = ParserContext.new<Unit, TagType>("test")
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())

        // Act
        val actual = PegExpressionRunner(PegExpression(listOf(seq), UUID.randomUUID())).parse(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
    }

    @Test
    fun `return first matched sequence`() {
        // Arrange
        val context = ParserContext.new<Unit, TagType>("test")
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegLiteralPrimary("lit", UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())
        val suffix2 = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix2 = PegNakedPrefix(suffix2, UUID.randomUUID())
        val seq2 = PegSequence(listOf(prefix2), UUID.randomUUID())

        // Act
        val actual = PegExpressionRunner(PegExpression(listOf(seq, seq2), UUID.randomUUID())).parse(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
    }

    @Test
    fun `fail if all sequences are failed`() {
        // Arrange
        val context = ParserContext.new<Unit, TagType>("test")
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegLiteralPrimary("lit", UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())
        val suffix2 = PegNakedSuffix<Unit, TagType>(PegClassPrimary(setOf('f'), UUID.randomUUID()), UUID.randomUUID())
        val prefix2 = PegNakedPrefix(suffix2, UUID.randomUUID())
        val seq2 = PegSequence(listOf(prefix2), UUID.randomUUID())

        // Act
        val actual = PegExpressionRunner(PegExpression(listOf(seq, seq2), UUID.randomUUID())).parse(source, context)

        // Assert
        assertThat(actual.getOrNull()).isNull()
    }
}
