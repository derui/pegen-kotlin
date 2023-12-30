package io.github.derui.pegen.core.parsing

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.parser.ErrorInfo
import io.github.derui.pegen.core.parser.ParserSource
import io.github.derui.pegen.core.support.Err
import io.github.derui.pegen.core.support.Ok
import org.junit.jupiter.api.Test

class ParserSourceTest {
    @Test
    fun `should be able to get next character without cursor change`() {
        // Arrange
        val context = ParserSource.newWith("test")

        // Act
        val actual = context.peekChar()

        // Assert
        assertThat(actual).isEqualTo(Ok('t'))
    }

    @Test
    fun `should return error info if can not read anymore`() {
        // Arrange
        val context = ParserSource.newWith("test")
        context.advanceWhile { true }

        // Act
        val actual = context.peekChar()

        // Assert
        assertThat(actual).isEqualTo(Err(ErrorInfo.from("Unexpected end of input", context.position)))
        assertThat(context.position.line).isEqualTo(1)
        assertThat(context.position.column).isEqualTo(5)
    }

    @Test
    fun `should be able to get new context`() {
        // Arrange
        val source = ParserSource.newWith("test")

        // Act
        val (txt, newSource) = source.advanceWhile { it == 't' }

        // Assert
        assertThat(newSource.position).isEqualTo(source.position)
        assertThat(txt).isEqualTo("t")
    }
}
