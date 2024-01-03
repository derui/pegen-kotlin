package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.parser.ParsingResult.Companion.asString
import io.github.derui.pegen.core.parser.ParsingResult.Companion.asType
import io.github.derui.pegen.core.support.getOrNull
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
            Pegen()<Data, Tag> {
                exp(s(+"a", +"b") tagged Tag.TagName, +"test")
            } constructAs { Data(it.tagged(Tag.TagName)?.asString() ?: "not found") }
        val parser = Generator(GeneratorOption { it.enableDebug() }).generateParserFrom(syntax)

        // Act
        val actual = parser.parse("ab")
        println(parser.printer().print())

        // Assert
        assertThat(actual.getOrNull()?.asType<Data>()).isEqualTo(Data("ab"))
    }
}
