package com.gchristov.thecodinglove.kmpsearchdata.parse

import com.gchristov.thecodinglove.kmpsearchdata.Post

interface PostParser {
    suspend fun parseTotalPosts(content: String): Int

    suspend fun parsePosts(content: String): List<Post>
}