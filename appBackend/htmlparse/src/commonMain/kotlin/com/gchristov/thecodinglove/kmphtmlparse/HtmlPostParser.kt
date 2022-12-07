package com.gchristov.thecodinglove.kmphtmlparse

import arrow.core.Either
import com.gchristov.thecodinglove.kmphtmlparsedata.HtmlPost

interface HtmlPostParser {
    suspend fun parseTotalPosts(content: String): Either<Exception, Int>

    suspend fun parsePosts(content: String): Either<Exception, List<HtmlPost>>
}