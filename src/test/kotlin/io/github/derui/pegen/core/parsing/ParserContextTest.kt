package io.github.derui.pegen.core.parsing

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import org.junit.jupiter.api.Test

class ParserContextTest {
    @Test
    fun `should be able to get next character`() {
        // Arrange
        val context = ParserContext.startOf<Unit>("test")

        // Act
        val actual = context.readChar()

        // Assert
        assertThat(actual).isEqualTo(Ok('t'))
    }

    @Test
    fun `should return error info if can not read anymore`() {
        // Arrange
        val context = ParserContext.startOf<Unit>("test")
        context.readChar()
        context.readChar()
        context.readChar()
        context.readChar()

        // Act
        val actual = context.readChar()

        // Assert
        assertThat(actual).isEqualTo(Err(ErrorInfo(listOf("Unexpected end of input"), context.position)))
        assertThat(context.position.line).isEqualTo(1)
        assertThat(context.position.column).isEqualTo(5)
    }

    @Test
    fun `should be able to get new context`() {
        // Arrange
        val context = ParserContext.startOf<Unit>("test")
        context.readChar()

        // Act
        val actual = context.newContext()

        // Assert
        assertThat(actual.position).isEqualTo(context.position)
    }

    @Test
    fun `should be able to get parsed string in context`() {
        // Arrange
        val context = ParserContext.startOf<Unit>("test")
        context.readChar()
        context.readChar()

        // Act
        val actual = context.parsed()

        // Assert
        assertThat(actual).isEqualTo("te")
    }

    @Test
    fun `new context should not reflect original context parsed information`() {
        // Arrange
        val context = ParserContext.startOf<Unit>("test")
        context.readChar()
        context.readChar()

        // Act
        val actual = context.newContext().parsed()

        // Assert
        assertThat(actual).isEqualTo("")
    }
}
