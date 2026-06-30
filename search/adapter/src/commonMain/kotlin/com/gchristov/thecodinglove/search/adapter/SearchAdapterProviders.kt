package com.gchristov.thecodinglove.search.adapter

import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlTotalPostsUseCase(): ParseHtmlTotalPostsUseCase

expect fun provideParseHtmlPostsUseCase(): ParseHtmlPostsUseCase
