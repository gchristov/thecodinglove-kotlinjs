package com.gchristov.thecodinglove.kmphtmlparse

import kotlinx.coroutines.Dispatchers

internal actual fun provideHtmlPostParser(): HtmlPostParser = RealHtmlPostParser(
    dispatcher = Dispatchers.Default
)