package com.gchristov.thecodinglove.common.kotlin

fun requireModule(module: String) = require(module)

private external fun require(module: String): dynamic

external class Buffer {
    fun toString(encoding: String): String

    companion object {
        fun from(
            any: Any,
            encoding: dynamic = definedExternally
        ): Buffer
    }
}

external val __dirname: dynamic

external val process: dynamic