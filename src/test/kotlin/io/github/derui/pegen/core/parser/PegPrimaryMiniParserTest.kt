package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDefinition
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegExpression
import io.github.derui.pegen.core.lang.PegGroupPrimary
import io.github.derui.pegen.core.lang.PegIdentifierPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PegPrimaryMiniParserTest {
    private enum class TagType

    @Nested
    inner class Dot {
        @Test
        fun `parse dot primary`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegDotPrimary<Unit, TagType>(UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
        }

        @Test
        fun `fail if empty`() {
            // Arrange
            val source = ParserSource.newWith("")
            val primary = PegDotPrimary<Unit, TagType>(UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class Literal {
        @Test
        fun `parse literal primary`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegLiteralPrimary<Unit, TagType>("te", UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("te", ParserSource.newWith("st")))
        }

        @Test
        fun `empty literal is always valid`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegLiteralPrimary<Unit, TagType>("", UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", ParserSource.newWith("test")))
        }

        @Test
        fun `fail if literal is not match`() {
            // Arrange
            val source = ParserSource.newWith("fail")
            val primary = PegLiteralPrimary<Unit, TagType>("te", UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class CharacterClass {
        @Test
        fun `parse class primary`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
        }

        @Test
        fun `fail if character class is not match`() {
            // Arrange
            val source = ParserSource.newWith("fail")
            val primary = PegClassPrimary<Unit, TagType>(setOf('a'), UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class Group {
        @Test
        fun `parse group primary`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
            val seq = PegSequence(listOf(prefix), UUID.randomUUID())
            val expr = PegExpression(listOf(seq), UUID.randomUUID())
            val primary = PegGroupPrimary(expr, UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual = PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
        }
    }

    @Nested
    inner class Identifier {
        @Test
        fun `parse definition`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
            val seq = PegSequence(listOf(prefix), UUID.randomUUID())
            val expr = PegExpression(listOf(seq), UUID.randomUUID())
            val primary = PegIdentifierPrimary(PegDefinition(UUID.randomUUID(), expr, {}), UUID.randomUUID())
            val context = ParserContext.new(primary)

            // Act
            val actual =
                PegPrimaryMiniParser.run(primary, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.constructedAs(Unit, ParserSource.newWith("est")))
        }
    }
}
