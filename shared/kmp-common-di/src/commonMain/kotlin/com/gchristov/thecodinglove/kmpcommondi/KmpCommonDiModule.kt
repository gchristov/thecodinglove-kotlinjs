package com.gchristov.thecodinglove.kmpcommondi

import org.kodein.di.DI

object KmpCommonDiModule : DiModule() {
    override fun name() = "kmp-common-di"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            // Use this for any common dependencies
        }
    }
}