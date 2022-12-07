package com.gchristov.thecodinglove.htmlparse

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object HtmlParseModule : DiModule() {
    override fun name() = "htmlparse"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHtmlPostParser() }
        }
    }

    private fun provideHtmlPostParser(): HtmlPostParser = RealHtmlPostParser(
        dispatcher = Dispatchers.Default
    )
}