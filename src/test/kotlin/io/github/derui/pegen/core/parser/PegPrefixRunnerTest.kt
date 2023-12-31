package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.lang.PegAndPrefix
import io.github.derui.pegen.core.lang.PegClassPrimary
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegNotPrefix
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PegPrefixRunnerTest {
    private enum class TagType

    @Nested
    inner class AndPrefix {
        @Test
        fun `parse and prefix`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegAndPrefix(suffix, UUID.randomUUID())

            // Act
            val actual = PegPrefixRunner.run(prefix, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", ParserSource.newWith("test")))
        }

        @Test
        fun `fail if suffix is not match`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegClassPrimary(setOf('a'), UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegAndPrefix(suffix, UUID.randomUUID())

            // Act
            val actual = PegPrefixRunner.run(prefix, source, context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class NotPrefix {
        @Test
        fun `run not prefix`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegClassPrimary(setOf('a'), UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegNotPrefix(suffix, UUID.randomUUID())

            // Act
            val actual = PegPrefixRunner.run(prefix, source, context)

            // Assert
            assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("", ParserSource.newWith("test")))
        }

        @Test
        fun `fail if suffix is not match`() {
            // Arrange
            val context = ParserContext.new<Unit, TagType>()
            val source = ParserSource.newWith("test")
            val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
            val prefix = PegNotPrefix(suffix, UUID.randomUUID())

            // Act
            val actual = PegPrefixRunner.run(prefix, source, context)

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }
}
