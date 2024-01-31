package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Test

class DefinitionTest {
    private enum class Tag {
        TagName,
    }

    @Test
    fun `generate parser with Def object`() {
        // Arrange
        val syntax =
            object : Pegen.Def<String, Tag>({
                Pegen<String, Tag> {
                    +"a" tagged Tag.TagName
                } constructAs {
                    it.tagged(Tag.TagName)[0]?.read?.let { s -> "$s parsed" } ?: "not found"
                }
            }) {}

        val parser = Generator(GeneratorOption { it.enableDebug() }).generateParserFrom(syntax)

        // Act
        val actual = parser.parse("ab")

        // Assert
        assertThat(actual.get().value()).isEqualTo("a parsed")
    }
}
