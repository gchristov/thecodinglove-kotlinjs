package com.gchristov.thecodinglove.htmlparsedata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.RealParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.RealParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object HtmlParseDataModule : DiModule() {
    override fun name() = "html-parse-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideParseHtmlTotalPostsUseCase(log = instance()) }
            bindProvider { provideParseHtmlPostsUseCase(log = instance()) }
        }
    }

    private fun provideParseHtmlTotalPostsUseCase(
        log: Logger
    ): ParseHtmlTotalPostsUseCase = RealParseHtmlTotalPostsUseCase(
        dispatcher = Dispatchers.Default,
        log = log
    )

    private fun provideParseHtmlPostsUseCase(
        log: Logger
    ): ParseHtmlPostsUseCase = RealParseHtmlPostsUseCase(
        dispatcher = Dispatchers.Default,
        log = log
    )
}