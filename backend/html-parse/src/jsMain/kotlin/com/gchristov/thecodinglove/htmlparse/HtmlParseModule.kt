package com.gchristov.thecodinglove.htmlparse

import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object HtmlParseModule : DiModule() {
    override fun name() = "htmlparse"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHtmlPostParser() }
        }
    }

    private fun provideHtmlPostParser(): HtmlPostParser = RealHtmlPostParser(
        dispatcher = Dispatchers.Default
    )
}