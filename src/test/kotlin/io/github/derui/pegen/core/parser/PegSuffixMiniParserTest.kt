package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegQuestionSuffix
import io.github.derui.pegen.core.lang.PegStarSuffix
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PegSuffixMiniParserTest {
    private enum class TagType

    @Nested
    inner class Question {
        @Test
        fun `parse dot primary`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())
            val suffix = PegQuestionSuffix(primary, UUID.randomUUID())
            val context = ParserContext.new(suffix)

            // Act
            val actual = PegSuffixMiniParser.run(suffix, source, context, recorder)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
        }

        @Test
        fun `success if not match`() {
            // Arrange
            val source = ParserSource.newWith("fo")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())
            val suffix = PegQuestionSuffix(primary, UUID.randomUUID())
            val context = ParserContext.new(suffix)

            // Act
            val actual = PegSuffixMiniParser.run(suffix, source, context, recorder)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", source))
        }
    }

    @Nested
    inner class Star {
        @Test
        fun `parse star suffix`() {
            // Arrange
            val source = ParserSource.newWith("test")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())
            val suffix = PegStarSuffix(primary, UUID.randomUUID())
            val context = ParserContext.new(suffix)

            // Act
            val actual = PegSuffixMiniParser.run(suffix, source, context, recorder)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("te", ParserSource.newWith("st")))
        }

        @Test
        fun `success if not match`() {
            // Arrange
            val source = ParserSource.newWith("fo")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())
            val suffix = PegStarSuffix(primary, UUID.randomUUID())
            val context = ParserContext.new(suffix)

            // Act
            val actual = PegSuffixMiniParser.run(suffix, source, context, recorder)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", source))
        }
    }
}
