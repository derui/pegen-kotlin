package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.parser.ParsingResult.Companion.asString
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Test

class GeneratorTest {
    @Test
    fun `generate parser with simple literal matching`() {
        // Arrange
        val parser =
            Generator<String, Unit> {
                exp(s(+"literal"))
            }

        // Act
        val actual = parser.parse("literal")

        // Assert
        assertThat(actual.get().asString()).isEqualTo("literal")
    }

    @Test
    fun `generate parser with some prefix`() {
        // Arrange
        val parser =
            Generator<String, Unit> {
                exp(s(many1(+"a"), +"b"))
            }

        // Act
        val actual = parser.parse("aaab")

        // Assert
        assertThat(actual.get().asString()).isEqualTo("aaab")
    }
}
