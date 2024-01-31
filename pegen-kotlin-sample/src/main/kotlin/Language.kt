package io.github

import io.github.derui.pegen.core.Pegen

sealed class Node

class Expr(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>({
        Pegen {
            Sum() tagged "sum"
        } constructAs {
            val sum = it.tagged("sum")[0]?.value() as? Sum
            Expr(sum?.value ?: 0L)
        }
    })
}

class Sum(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>({
        Pegen {
            s(Product() tagged "initial", many(g(s(g(+"+" / +"-") tagged "op", Product() tagged "others"))))
        } constructAs {
            val initial = it.tagged("initial").firstAsType<Product>()?.value ?: 0
            val values =
                it.tagged("op").asList().mapIndexed { index, ret ->
                    val other = it.tagged("others")[index]?.value() as? Product

                    when (ret.read) {
                        "+" -> other?.value ?: 0
                        "-" -> -(other?.value ?: 0)
                        else -> 0
                    }
                }

            Sum(initial + values.sum())
        }
    })
}

class Product(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>({
        Pegen {
            s(Power() tagged "initial", many(g(s(g(+"*" / +"/") tagged "op", Power() tagged "others"))))
        } constructAs {
            val initial = it.tagged("initial").firstAsType<Power>()?.value ?: 0
            val product =
                it.tagged("op").asList().foldIndexed(initial) { index, sum, ret ->
                    val other = it.tagged("others")[index]?.value() as? Power

                    when (ret.read) {
                        "*" -> sum * (other?.value ?: 1)
                        "/" -> sum / (other?.value ?: 1)
                        else -> sum
                    }
                }

            Product(product)
        }
    })
}

class Power(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>({
        Pegen {
            s(Value() tagged "initial", opt(g(s(+"^", Power() tagged "powered"))))
        } constructAs {
            val initial = it.tagged("initial").firstAsType<Value>()?.value ?: 0
            val power = it.tagged("powered").firstAsType<Power>()?.value ?: 1

            if (power == 0L) {
                Power(1)
            } else {
                var accum = initial
                repeat((power - 1L).toInt()) {
                    accum *= initial
                }
                Power(accum)
            }
        }
    })
}

class Value(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>({
        Pegen {
            (many1(cls { +('0'..'9') }) tagged "digits") / s(+"(", Expr() tagged "expr", +")")
        } constructAs {
            val digits = it.tagged("digits").firstRead()?.toLong()
            val expr = it.tagged("expr").firstAsType<Expr>()?.value

            Value(digits ?: expr ?: 0)
        }
    })
}
