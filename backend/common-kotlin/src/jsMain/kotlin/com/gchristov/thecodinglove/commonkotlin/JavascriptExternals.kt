package com.gchristov.thecodinglove.commonkotlin

fun requireModule(module: String) = require(module)

private external fun require(module: String): dynamic

external object Buffer {
    fun from(
        any: Any,
        encoding: dynamic = definedExternally
    ): Buffer
}

external val __dirname: dynamic

external val process: dynamic