package com.gchristov.thecodinglove.htmlparsedata

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
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
}

expect fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase