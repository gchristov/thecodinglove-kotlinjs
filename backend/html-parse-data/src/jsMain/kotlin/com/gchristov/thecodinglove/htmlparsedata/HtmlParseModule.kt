package com.gchristov.thecodinglove.htmlparsedata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object HtmlParseDataModule : DiModule() {
    override fun name() = "html-parse-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideHtmlPostParser(log = instance()) }
        }
    }

    private fun provideHtmlPostParser(
        log: Logger
    ): ParseHtmlPostsUseCase = RealParseHtmlPostsUseCase(
        dispatcher = Dispatchers.Default,
        log = log
    )
}