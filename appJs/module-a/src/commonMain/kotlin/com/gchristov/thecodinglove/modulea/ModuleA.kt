package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.kmpmoduleb.KmpModuleB

external fun require(module:String) : dynamic

class ModuleA {
    fun name(): String {
        val express = require("express")
        val expressApp = express()
        return "ModuleA${expressApp::class.simpleName} + ${KmpModuleB().name()}"
    }
}