package com.gchristov.thecodinglove.htmlparse

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object HtmlParseModule : DiModule() {
    override fun name() = "htmlparse"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHtmlPostParser(log = instance()) }
        }
    }

    private fun provideHtmlPostParser(log: Logger): HtmlPostParser = RealHtmlPostParser(
        dispatcher = Dispatchers.Default,
        log = log
    )
}