package com.gchristov.thecodinglove.htmlparsedata

import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.RealParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.RealParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider

object HtmlParseDataModule : DiModule() {
    override fun name() = "html-parse-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideParseHtmlTotalPostsUseCase() }
            bindProvider { provideParseHtmlPostsUseCase() }
        }
    }

    private fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase = RealParseHtmlTotalPostsUseCase(
        dispatcher = Dispatchers.Default
    )

    private fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase = RealParseHtmlPostsUseCase(
        dispatcher = Dispatchers.Default
    )
}