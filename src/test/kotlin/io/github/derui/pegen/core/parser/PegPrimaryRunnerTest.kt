package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PegPrimaryRunnerTest {
    private enum class TagType : Tag

    @Nested
    inner class Dot {
        @Test
        fun `parse dot primary`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("test")

            // Act
            val actual = PegPrimaryRunner.run(PegDotPrimary<Unit, TagType>(UUID.randomUUID()), context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t"))
        }

        @Test
        fun `fail if empty`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("")

            // Act
            val actual = PegPrimaryRunner.run(PegDotPrimary<Unit, TagType>(UUID.randomUUID()), context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class Literal {
        @Test
        fun `parse literal primary`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("test")

            // Act
            val actual = PegPrimaryRunner.run(PegLiteralPrimary<Unit, TagType>("te", UUID.randomUUID()), context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("te"))
        }

        @Test
        fun `fail if literal is not match`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("fail")

            // Act
            val actual = PegPrimaryRunner.run(PegLiteralPrimary<Unit, TagType>("te", UUID.randomUUID()), context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class CharacterClass {
        @Test
        fun `parse class primary`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("test")

            // Act
            val actual = PegPrimaryRunner.run(PegClassPrimary<Unit, TagType>(setOf('t', 'e'), UUID.randomUUID()), context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t"))
        }

        @Test
        fun `fail if literal is not match`() {
            // Arrange
            val context = ParserContext.newWith<Unit>("fail")

            // Act
            val actual = PegPrimaryRunner.run(PegClassPrimary<Unit, TagType>(setOf('a'), UUID.randomUUID()), context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }
}
