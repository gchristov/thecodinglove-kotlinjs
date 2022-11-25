package com.gchristov.thecodinglove.kmphtmlparse

import com.gchristov.thecodinglove.kmphtmlparsedata.HtmlPost

interface HtmlPostParser {
    suspend fun parseTotalPosts(content: String): Int

    suspend fun parsePosts(content: String): List<HtmlPost>
}