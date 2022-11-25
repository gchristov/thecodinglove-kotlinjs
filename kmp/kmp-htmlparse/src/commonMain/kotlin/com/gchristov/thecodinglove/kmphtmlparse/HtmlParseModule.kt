package com.gchristov.thecodinglove.kmphtmlparse

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import org.kodein.di.DI
import org.kodein.di.bindProvider

object HtmlParseModule : DiModule() {
    override fun name() = "kmp-htmlparse"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHtmlPostParser() }
        }
    }
}

internal expect fun provideHtmlPostParser(): HtmlPostParser