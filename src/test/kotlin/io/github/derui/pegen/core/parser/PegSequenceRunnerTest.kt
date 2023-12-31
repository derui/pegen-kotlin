package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.github.derui.pegen.core.Tag
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.get
import org.junit.jupiter.api.Test
import java.util.UUID

class PegSequenceRunnerTest {
    private enum class TagType : Tag

    @Test
    fun `parse sequence`() {
        // Arrange
        val context = ParserContext.new<Unit>()
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())

        // Act
        val actual = PegSequenceRunner(seq).run(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
    }
}
