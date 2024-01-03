package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.parser.ParsingResult.Companion.asString
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GeneratorTest {
    private val option =
        GeneratorOption {
            it.enableDebug()
        }

    @Nested
    inner class PrimarySyntax {
        @Nested
        inner class Dot {
            @Test
            fun `parse dot primary`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(s(dot))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("t")
            }

            @Test
            fun `fail if empty`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(s(dot))
                    }

                // Act
                val actual = parser.parse("")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }

        @Nested
        inner class Literal {
            @Test
            fun `parse literal primary`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(s(+"literal"))
                    }

                // Act
                val actual = parser.parse("literal")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("literal")
            }

            @Test
            fun `empty literal is always valid`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(s(+""))
                    }

                // Act
                val actual = parser.parse("literal")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("")
            }

            @Test
            fun `fail if literal is not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit> {
                        exp(s(+"literal"))
                    }

                // Act
                val actual = parser.parse("parser")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }

        @Nested
        inner class CharacterClass {
            @Test
            fun `parse class primary`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit> {
                        exp(
                            s(
                                cls {
                                    +('a'..'z')
                                },
                            ),
                        )
                    }

                // Act
                val actual = parser.parse("abcd")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("a")
            }

            @Test
            fun `fail if not matched`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit> {
                        exp(
                            s(
                                cls {
                                    +('a'..'z')
                                },
                            ),
                        )
                    }

                // Act
                val actual = parser.parse("Abcd")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }

        @Nested
        inner class Group {
            @Test
            fun `parse group primary`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(
                            s(
                                cls {
                                    +('a'..'z')
                                },
                            ),
                        )
                    }

                // Act
                val actual = parser.parse("abcd")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("a")
            }
        }

        @Nested
        inner class Identifier {
            @Test
            fun `parse definition`() {
                // Arrange
                val other =
                    Generator<String, Unit> {
                        exp(s(+"test"))
                    } constructAs { "foo" }

                val parser =
                    Generator.generateParser(option) {
                        exp(s(ident(other)))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("test")
            }
        }
    }

    @Nested
    inner class Suffix {
        @Nested
        inner class Question {
            @Test
            fun `parse dot primary`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(opt(cls { +('e'..'t') }))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("t")
            }

            @Test
            fun `success if not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(opt(cls { +"te" }))
                    }

                // Act
                val actual = parser.parse("fo")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("")
            }
        }

        @Nested
        inner class Star {
            @Test
            fun `parse star suffix`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(many(cls { +"te" }))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("te")
            }

            @Test
            fun `success if not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(many(cls { +('s'..'t') }))
                    }

                // Act
                val actual = parser.parse("foo")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("")
            }
        }

        @Nested
        inner class Plus {
            @Test
            fun `parse star suffix`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(many1(cls { +('e'..'t') }))
                    }

                // Act
                val actual = parser.parse("teaa")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("te")
            }

            @Test
            fun `fail if not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(
                            many1(
                                cls {
                                    +"t"
                                    +"e"
                                },
                            ),
                        )
                    }

                // Act
                val actual = parser.parse("foo")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }
    }

    @Nested
    inner class Prefix {
        @Nested
        inner class AndPrefix {
            @Test
            fun `parse and prefix`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(and(dot))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("")
            }

            @Test
            fun `fail if suffix is not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(and(+"a"))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }

        @Nested
        inner class NotPrefix {
            @Test
            fun `success if suffix is not match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(not(+"abc"))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().asString()).isEqualTo("")
            }

            @Test
            fun `fail if suffix is match`() {
                // Arrange
                val parser =
                    Generator.generateParser<String, Unit>(option) {
                        exp(not(dot))
                    }

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.getOrNull()).isNull()
            }
        }
    }

    @Nested
    inner class Sequence {
        @Test
        fun `parse sequence`() {
            // Arrange
            val parser =
                Generator.generateParser<String, Unit>(option) {
                    exp(s(+"a", +"b"))
                }

            // Act
            val actual = parser.parse("abc")

            // Assert
            assertThat(actual.get().asString()).isEqualTo("ab")
        }

        @Test
        fun `fail if any prefix is failed in sequence`() {
            // Arrange
            val parser =
                Generator.generateParser<String, Unit>(option) {
                    exp(s(+"a", +"b"))
                }

            // Act
            val actual = parser.parse("acb")

            // Assert
            assertThat(actual.getOrNull()).isNull()
        }
    }

    @Nested
    inner class Expression {
        @Test
        fun `parse expression`() {
            // Arrange
            val parser =
                Generator.generateParser<String, Unit>(option) {
                    exp(s(+"a", +"b"))
                }

            // Act
            val actual = parser.parse("abc")

            // Assert
            assertThat(actual.get().asString()).isEqualTo("ab")
        }

        @Test
        fun `return first matched sequence`() {
            // Arrange
            val parser =
                Generator.generateParser<String, Unit>(option) {
                    exp(s(+"a", +"b"), +"test")
                }

            // Act
            val actual = parser.parse("test")

            // Assert
            assertThat(actual.getOrNull()?.asString()).isEqualTo("test")
        }

        @Test
        fun `fail if all sequences are failed`() {
            // Arrange
            val parser =
                Generator.generateParser<String, Unit>(option) {
                    exp(s(+"a", +"b"), +"test")
                }

            // Act
            val actual = parser.parse("foo")

            // Assert
            assertThat(actual.getOrNull()?.asString()).isNull()
        }
    }
}
