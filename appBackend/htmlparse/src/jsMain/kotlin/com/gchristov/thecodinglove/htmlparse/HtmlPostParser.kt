package com.gchristov.thecodinglove.htmlparse

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparsedata.HtmlPost

interface HtmlPostParser {
    suspend fun parseTotalPosts(content: String): Either<Throwable, Int>

    suspend fun parsePosts(content: String): Either<Throwable, List<HtmlPost>>
}