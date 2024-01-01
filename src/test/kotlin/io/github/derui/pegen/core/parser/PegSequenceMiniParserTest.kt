package io.github.derui.pegen.core.parser

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.lang.PegDotPrimary
import io.github.derui.pegen.core.lang.PegLiteralPrimary
import io.github.derui.pegen.core.lang.PegNakedPrefix
import io.github.derui.pegen.core.lang.PegNakedSuffix
import io.github.derui.pegen.core.lang.PegSequence
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Test
import java.util.UUID

class PegSequenceMiniParserTest {
    private enum class TagType

    @Test
    fun `parse sequence`() {
        // Arrange
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix), UUID.randomUUID())
        val context = ParserContext.new(seq)

        // Act
        val actual = PegSequenceMiniParser(seq).parse(source, context)

        // Assert
        assertThat(actual.get()).isEqualTo(ParsingResult.rawOf("t", ParserSource.newWith("est")))
    }

    @Test
    fun `fail if any prefix is failed in sequence`() {
        // Arrange
        val source = ParserSource.newWith("test")
        val suffix = PegNakedSuffix<Unit, TagType>(PegDotPrimary(UUID.randomUUID()), UUID.randomUUID())
        val prefix = PegNakedPrefix(suffix, UUID.randomUUID())
        val suffix2 = PegNakedSuffix<Unit, TagType>(PegLiteralPrimary("baz", UUID.randomUUID()), UUID.randomUUID())
        val prefix2 = PegNakedPrefix(suffix2, UUID.randomUUID())
        val seq = PegSequence(listOf(prefix, prefix2), UUID.randomUUID())
        val context = ParserContext.new(seq)

        // Act
        val actual = PegSequenceMiniParser(seq).parse(source, context)

        // Assert
        assertThat(actual.getOrNull()).isNull()
    }
}
