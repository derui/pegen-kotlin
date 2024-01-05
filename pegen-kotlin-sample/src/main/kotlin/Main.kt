package io.github

import io.github.derui.pegen.core.Generator
import io.github.derui.pegen.core.support.get

fun main() {
    val parser = Generator().generateParserFrom(Expr)

    val result = parser.parse("(1+2+3+4+5+6+7+8+9+10)*2")
    val value = (result.get().value() as Expr).value
    println(value)
}
