package io.github.derui.pegen.core

/**
 * Option for [Pegen]
 */
class GeneratorOption internal constructor() {
    var debug: Boolean = false
        private set

    companion object {
        val default = GeneratorOption()

        operator fun invoke(init: Companion.(GeneratorOption) -> Unit): GeneratorOption {
            val option = default
            this.init(option)
            return option
        }

        fun GeneratorOption.enableDebug() {
            debug = true
        }
    }
}
