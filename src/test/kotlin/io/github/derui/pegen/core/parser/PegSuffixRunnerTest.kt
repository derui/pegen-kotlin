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

class PegSuffixRunnerTest {
    private enum class TagType

    @Nested
    inner class Question {
        @Test
        fun `parse dot primary`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())

            // Act
            val actual = PegSuffixRunner.run(PegQuestionSuffix(primary, UUID.randomUUID()), source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
        }

        @Test
        fun `success if not match`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("fo")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())

            // Act
            val actual = PegSuffixRunner.run(PegQuestionSuffix(primary, UUID.randomUUID()), source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", source))
        }
    }

    @Nested
    inner class Star {
        @Test
        fun `parse star suffix`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())

            // Act
            val actual = PegSuffixRunner.run(PegStarSuffix(primary, UUID.randomUUID()), source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("te", ParserSource.newWith("st")))
        }

        @Test
        fun `success if not match`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("fo")
            val primary = PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID())

            // Act
            val actual = PegSuffixRunner.run(PegStarSuffix(primary, UUID.randomUUID()), source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", source))
        }
    }
}
