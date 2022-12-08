package com.gchristov.thecodinglove.kmpcommonkotlin

fun requireModule(module: String) = require(module)

external var exports: dynamic

private external fun require(module: String): dynamic