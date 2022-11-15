package com.gchristov.thecodinglove.modulea

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpmoduleb.BModule
import com.gchristov.thecodinglove.kmpmoduleb.KmpModuleB
import org.kodein.di.DI
import org.kodein.di.bindProvider

external fun require(module:String) : dynamic

object AModule : DiModule() {
    override fun name() = "module-a"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { ModuleA(moduleB = inject()) }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(BModule.module)
    }

    fun injectModuleA(): ModuleA = inject()
}

data class ModuleA(
    val moduleB: KmpModuleB
) {
    fun name(): String {
        val express = require("express")
        val expressApp = express()
        return "ModuleA${expressApp::class.simpleName} + $moduleB"
    }
}