package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Test

class ConstructionTest {
    private data class Data(
        val value: String,
    )

    private enum class Tag {
        TagName,
    }

    @Test
    fun `construct with tag and class`() {
        // Arrange
        val syntax =
            Pegen<Data, Tag>().define {
                exp(s(+"a", +"b") tagged Tag.TagName, +"test")
            } constructAs { Data(it.tagged(Tag.TagName)?.asString() ?: "not found") }
        val parser = Generator(GeneratorOption { it.enableDebug() }).generateParserFrom(syntax)

        // Act
        val actual = parser.parse("ab")

        // Assert
        assertThat(actual.get().asType<Data>()).isEqualTo(Data("ab"))
    }
}
