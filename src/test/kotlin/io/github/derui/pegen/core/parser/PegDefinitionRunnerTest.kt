package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Test
import java.util.UUID

class PegDefinitionRunnerTest {
    private enum class TagType {
        Dot,
    }

    @Test
    fun `parse definition`() {
        // Arrange
        val context = ParserContext.new<Unit, TagType>("test")
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())
        val expr = PegExpression(listOf(seq), UUID.randomUUID())

        // Act
        val actual = PegDefinitionRunner(PegDefinition(UUID.randomUUID(), expr, {})).parse(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.constructedAs(Unit, ParserSource.newWith("est")))
    }

    @Test
    fun `should be able to create instance via context`() {
        // Arrange
        val context = ParserContext.new<String, TagType>("test")
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<String, TagType>(PegDotPrimary(UUID.randomUUID(), tag = TagType.Dot), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())
        val expr = PegExpression(listOf(seq), UUID.randomUUID())

        // Act
        val actual =
            PegDefinitionRunner(
                PegDefinition(UUID.randomUUID(), expr) {
                    ParsingResult { it.tagged(TagType.Dot)?.asString() ?: error("not found") }
                },
            ).parse(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.constructedAs("t", ParserSource.newWith("est")))
    }
}
