package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.modulea.AModule

class Messenger {
    fun message() =
        "Hello from multi-module Kotlin JS! Nested modules: ${AModule.injectModuleA().name()}!"
}