package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Nested
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
            } constructAs { Data(it.tagged(Tag.TagName)[0]?.read ?: "not found") }
        val parser = Generator(GeneratorOption { it.enableDebug() }).generateParserFrom(syntax)

        // Act
        val actual = parser.parse("ab")

        // Assert
        assertThat(actual.get().value()).isEqualTo(Data("ab"))
    }

    @Nested
    inner class Tagging {
        @Test
        fun `get tagging result as string from any syntax`() {
            // Arrange
            val syntax =
                Pegen<Data, String>().define {
                    exp(s(+"a", +"b") tagged "tag", +"test")
                } constructAs { Data(it.tagged("tag")[0]?.read ?: "not found") }
            val parser = Generator().generateParserFrom(syntax)

            // Act
            val actual = parser.parse("ab")

            // Assert
            assertThat(actual.get().value()).isEqualTo(Data("ab"))
        }

        @Test
        fun `get tagging result as list from many syntax`() {
            // Arrange
            val syntax =
                Pegen<String, String>().define {
                    many(+"a") tagged "tag"
                } constructAs {
                    it.tagged("tag").asList().joinToString("/")
                }
            val parser = Generator().generateParserFrom(syntax)

            // Act
            val actual = parser.parse("aab")

            // Assert
            assertThat(actual.get().value()).isEqualTo("a/a")
        }
    }
}
