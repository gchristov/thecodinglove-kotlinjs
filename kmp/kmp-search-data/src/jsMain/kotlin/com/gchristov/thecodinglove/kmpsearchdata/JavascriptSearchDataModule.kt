package com.gchristov.thecodinglove.kmpsearchdata

import kotlinx.coroutines.Dispatchers

internal actual fun providePostParser(): PostParser =
    HtmlPostParser(dispatcher = Dispatchers.Default)