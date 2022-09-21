package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.modulea.ModuleA
import org.kodein.di.DI

class Messenger {
    fun message() = "Hello from multi-module Kotlin JS! Nested modules: ${ModuleA().name()}!"

    fun test() {
        val kodein = DI {

        }
    }
}