package com.gchristov.thecodinglove.kmpcommondi

import org.kodein.di.DI

object CommonDiModule : DiModule() {
    override fun name() = "kmp-common-di"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            // Use this for any common dependencies, eg logger
        }
    }
}