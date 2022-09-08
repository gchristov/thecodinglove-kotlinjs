package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.modulea.ModuleA

external fun require(module:String) : dynamic
external var exports: dynamic

fun main(args: Array<String>) {
    val message = "Hello from multi-module Kotlin JS! Nested modules: ${ModuleA().name()}!"
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        response.send(message)
    }
}