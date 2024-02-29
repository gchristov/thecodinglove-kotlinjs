package com.gchristov.thecodinglove.search.adapter

import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.NodeParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.NodeParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlTotalPostsUseCase
import kotlinx.coroutines.Dispatchers

actual fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase = NodeParseHtmlTotalPostsUseCase(
    dispatcher = Dispatchers.Default
)

actual fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase = NodeParseHtmlPostsUseCase(
    dispatcher = Dispatchers.Default
)