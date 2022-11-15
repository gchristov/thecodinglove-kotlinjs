package com.gchristov.thecodinglove.kmpmoduleb

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import org.kodein.di.DI
import org.kodein.di.bindProvider

object BModule : DiModule() {
    override fun name() = "kmp-module-b"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { KmpModuleB(value = 123) }
        }
    }
}

data class KmpModuleB(private val value: Int) {
    fun name(): String {
        return "KmpModuleB$value"
    }
}