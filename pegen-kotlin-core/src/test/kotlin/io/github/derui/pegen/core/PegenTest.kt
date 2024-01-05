package io.github.derui.pegen.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import io.github.derui.pegen.core.support.get
import io.github.derui.pegen.core.support.getOrNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PegenTest {
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
                val syntax =
                    Pegen<String, Unit>().define {
                        dot
                    }.constructAs { "foo" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("t")
                assertThat(actual.get().value()).isEqualTo("foo")
            }

            @Test
            fun `fail if empty`() {
                // Arrange
                val def =
                    Pegen<String, Unit>().define {
                        dot
                    }.constructAs { "" }
                val parser = Generator(option).generateParserFrom(def)

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
                val syntax =
                    Pegen<String, Unit>().define {
                        +"literal"
                    }.constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("literal")

                // Assert
                assertThat(actual.get().read).isEqualTo("literal")
                assertThat(actual.get().value()).isEqualTo("parsed")
            }

            @Test
            fun `empty literal is always valid`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        +""
                    }.constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("literal")

                // Assert
                assertThat(actual.get().read).isEqualTo("")
                assertThat(actual.get().value()).isEqualTo("parsed")
            }

            @Test
            fun `fail if literal is not match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        +"literal"
                    }.constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

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
                val syntax =
                    Pegen<String, Unit>().define {
                        cls {
                            +('a'..'z')
                        }
                    }.constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("abcd")

                // Assert
                assertThat(actual.get().read).isEqualTo("a")
                assertThat(actual.get().value()).isEqualTo("parsed")
            }

            @Test
            fun `fail if not matched`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        cls {
                            +('a'..'z')
                        }
                    }.constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

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
                val syntax =
                    Pegen<String, Unit>().define {
                        g(cls { +('a'..'z') })
                    } constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("abcd")

                // Assert
                assertThat(actual.get().read).isEqualTo("a")
                assertThat(actual.get().value()).isEqualTo("parsed")
            }
        }

        @Nested
        inner class Identifier {
            @Test
            fun `parse definition`() {
                // Arrange
                val other =
                    Pegen<String, Unit>().define {
                        +"test"
                    } constructAs { "foo" }

                val syntax =
                    Pegen<String, Unit>().define {
                        other()
                    } constructAs { "foo" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("test")
                assertThat(actual.get().value()).isEqualTo("foo")
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
                val syntax =
                    Pegen<String, Unit>().define {
                        opt(cls { +('e'..'t') })
                    } constructAs { "parsed" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("t")
                assertThat(actual.get().value()).isEqualTo("parsed")
            }

            @Test
            fun `success if not match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        opt(cls { +"te" })
                    } constructAs { "foo" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("fo")

                // Assert
                assertThat(actual.get().read).isEqualTo("")
                assertThat(actual.get().value()).isEqualTo("foo")
            }
        }

        @Nested
        inner class Star {
            @Test
            fun `parse star suffix`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        many(cls { +"te" })
                    } constructAs { "star" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("te")
                assertThat(actual.get().value()).isEqualTo("star")
            }

            @Test
            fun `success if not match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        many(cls { +('s'..'t') })
                    } constructAs { "star" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("foo")

                // Assert
                assertThat(actual.get().read).isEqualTo("")
            }
        }

        @Nested
        inner class Plus {
            @Test
            fun `parse star suffix`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        many1(cls { +('e'..'t') })
                    } constructAs { "plus" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("teaa")

                // Assert
                assertThat(actual.get().read).isEqualTo("te")
                assertThat(actual.get().value()).isEqualTo("plus")
            }

            @Test
            fun `fail if not match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        many1(
                            cls {
                                +"t"
                                +"e"
                            },
                        )
                    } constructAs { "" }
                val parser = Generator(option).generateParserFrom(syntax)

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
                val syntax =
                    Pegen<String, Unit>().define {
                        and(dot)
                    } constructAs { "and" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("")
                assertThat(actual.get().value()).isEqualTo("and")
            }

            @Test
            fun `fail if suffix is not match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        and(+"a")
                    } constructAs { "" }
                val parser = Generator(option).generateParserFrom(syntax)

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
                val syntax =
                    Pegen<String, Unit>().define {
                        not(+"abc")
                    } constructAs { "" }
                val parser = Generator(option).generateParserFrom(syntax)

                // Act
                val actual = parser.parse("test")

                // Assert
                assertThat(actual.get().read).isEqualTo("")
            }

            @Test
            fun `fail if suffix is match`() {
                // Arrange
                val syntax =
                    Pegen<String, Unit>().define {
                        not(dot)
                    } constructAs { "" }
                val parser = Generator(option).generateParserFrom(syntax)

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
            val syntax =
                Pegen<String, Unit>().define {
                    s(+"a", +"b")
                } constructAs { "ab" }
            val parser = Generator(option).generateParserFrom(syntax)

            // Act
            val actual = parser.parse("abc")

            // Assert
            assertThat(actual.get().read).isEqualTo("ab")
        }

        @Test
        fun `fail if any prefix is failed in sequence`() {
            // Arrange
            val syntax =
                Pegen<String, Unit>().define {
                    s(+"a", +"b")
                } constructAs { "" }
            val parser = Generator(option).generateParserFrom(syntax)

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
            val syntax =
                Pegen<String, Unit>().define {
                    s(+"a", +"b")
                } constructAs { "constructed" }
            val parser = Generator(option).generateParserFrom(syntax)

            // Act
            val actual = parser.parse("abc")

            // Assert
            assertThat(actual.get().read).isEqualTo("ab")
            assertThat(actual.get().value()).isEqualTo("constructed")
        }

        @Test
        fun `return first matched sequence`() {
            // Arrange
            val syntax =
                Pegen<String, Unit>().define {
                    s(+"a", +"b") / +"test"
                } constructAs { "" }
            val parser = Generator(option).generateParserFrom(syntax)

            // Act
            val actual = parser.parse("test")

            // Assert
            assertThat(actual.getOrNull()?.read).isEqualTo("test")
            assertThat(actual.getOrNull()?.value()).isEqualTo("")
        }

        @Test
        fun `fail if all sequences are failed`() {
            // Arrange
            val syntax =
                Pegen<String, Unit>().define {
                    s(+"a", +"b") / +"test"
                } constructAs { "" }
            val parser = Generator(option).generateParserFrom(syntax)

            // Act
            val actual = parser.parse("foo")

            // Assert
            assertThat(actual.getOrNull()?.read).isNull()
        }
    }
}
