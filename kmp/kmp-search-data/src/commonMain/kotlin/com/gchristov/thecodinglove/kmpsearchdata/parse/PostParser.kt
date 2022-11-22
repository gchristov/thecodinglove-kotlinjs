package com.gchristov.thecodinglove.kmpsearchdata.parse

import com.gchristov.thecodinglove.kmpsearchdata.Post

interface PostParser {
    suspend fun parseResultsCount(content: String): Int

    suspend fun parseResults(content: String): List<Post>
}