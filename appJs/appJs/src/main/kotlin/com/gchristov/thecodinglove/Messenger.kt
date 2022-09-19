package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.modulea.ModuleA

class Messenger {
    fun message() = "Hello from multi-module Kotlin JS! Nested modules: ${ModuleA().name()}!"
}