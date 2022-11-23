package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SearchDataModule : DiModule() {
    override fun name() = "kmp-search-data"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { providePostParser() }
        }
    }

    fun injectPostParser(): PostParser = inject()
}

internal expect fun providePostParser(): PostParser