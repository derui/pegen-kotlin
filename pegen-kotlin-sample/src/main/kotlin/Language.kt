package io.github

import io.github.derui.pegen.core.Pegen

sealed class Node

class Expr(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>(
        Pegen<Node, String>() define {
            Sum() tagged "sum"
        } constructAs {
            Expr(it.tagged("sum")[0]?.value() ?: 0L)
        },
    )
}

class Sum(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>(
        Pegen<Node, String>() define {
            s(Product(), many(g(s(g(+"+" / +"-"), Product()))))
        } constructAs {
            val initial = it.tagged("initial")?.asType<Product>()?.value ?: 0L
            val values =
                it.tagged("value")?.asType<List<Pair<String, Product>>>()?.map { it.second.value * if (it.first == "+") 1 else -1 }
                    ?: emptyList()
            TODO()
        },
    )
}

class Product(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>(
        Pegen<Node, String>().define {
            s(Power(), many(g(s(g(+"*" / +"/"), Power()))))
        } constructAs {
            TODO()
        },
    )
}

class Power(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>(
        Pegen<Node, String>().define {
            s(Value(), opt(s(+"^", Power())))
        } constructAs {
            TODO()
        },
    )
}

class Value(val value: Long) : Node() {
    companion object : Pegen.Def<Node, String>(
        Pegen<Node, String>().define {
            cls { '0'..'9' } / s(+"(", Expr(), +")")
        } constructAs {
            TODO()
        },
    )
}
