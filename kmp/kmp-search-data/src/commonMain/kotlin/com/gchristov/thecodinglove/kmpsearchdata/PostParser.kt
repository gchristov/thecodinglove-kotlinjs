package com.gchristov.thecodinglove.kmpsearchdata

interface PostParser {
    suspend fun parseTotalPosts(content: String): Int

    suspend fun parsePosts(content: String): List<Post>
}