package io.github.derui.pegen.core.support

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class ResultTest {
    @Test
    fun `should be able to get from Result`() {
        // Arrange
        val result = Ok(1)

        // Act
        val ret = result.getOrNull()

        // Assert
        assertThat(ret).isEqualTo(1)
    }

    @Test
    fun `should return null if it is error`() {
        // Arrange
        val result: Result<Int, String> = Err("error")

        // Act
        val ret = result.getOrNull()

        // Assert
        assertThat(ret).isNull()
    }

    @Test
    fun `should be able to map from Result`() {
        // Arrange
        val result = Ok(1)

        // Act
        val ret = result map { it + 1 }

        // Assert
        assertThat(ret.getOrNull()).isEqualTo(2)
    }

    @Test
    fun `should not be able to map from Error`() {
        // Arrange
        val result: Result<String, Int> = Err(1)

        // Act
        val ret = result map { it + 1 }

        // Assert
        assertThat(ret.getOrNull()).isNull()
    }
}
