package com.gchristov.thecodinglove.htmlparsedata

import com.gchristov.thecodinglove.htmlparsedata.usecase.JavascriptParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.JavascriptParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import kotlinx.coroutines.Dispatchers

actual fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase = JavascriptParseHtmlTotalPostsUseCase(
    dispatcher = Dispatchers.Default
)

actual fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase = JavascriptParseHtmlPostsUseCase(
    dispatcher = Dispatchers.Default
)