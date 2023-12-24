package io.github.derui.pegen.core.parsing

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isNotEqualTo
import org.junit.jupiter.api.Test

class PositionTest {
    @Test
    fun `should be able to advance`() {
        // Arrange

        // Act
        val position = Position.start()

        // Assert
        assertThat(position.line).isEqualTo(1)
        assertThat(position.column).isEqualTo(1)
    }

    @Test
    fun `should be able to forward`() {
        // Arrange

        // Act
        val position = Position.start().forward('a')

        // Assert
        assertThat(position.line).isEqualTo(1)
        assertThat(position.column).isEqualTo(2)
    }

    @Test
    fun `should be able to forward with newline`() {
        // Arrange

        // Act
        val position = Position.start().forward('a').forward('\n')

        // Assert
        assertThat(position.line).isEqualTo(2)
        assertThat(position.column).isEqualTo(1)
    }

    @Test
    fun `should be able to compare`() {
        // Arrange

        // Act
        val position1 = Position.start().forward('a').forward('\n')
        val position2 = Position.start().forward('a').forward('b')

        // Assert
        assertThat(position1).isGreaterThan(position2)
        assertThat(position2).isLessThan(position1)
        assertThat(position1).isNotEqualTo(position2)
    }
}
