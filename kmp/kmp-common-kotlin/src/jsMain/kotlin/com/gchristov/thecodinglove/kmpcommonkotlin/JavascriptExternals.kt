package com.gchristov.thecodinglove.kmpcommonkotlin

fun requireModule(module: String) = require(module)

private external fun require(module: String): dynamic