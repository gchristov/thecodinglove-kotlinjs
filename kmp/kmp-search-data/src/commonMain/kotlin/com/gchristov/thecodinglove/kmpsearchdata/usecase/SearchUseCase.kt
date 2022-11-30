package com.gchristov.thecodinglove.kmpsearchdata.usecase

import com.gchristov.thecodinglove.kmpsearchdata.model.Post

/**
Use-case to search for a random post, given a search session. This use-case:
- obtains the total results for the given query, if not provided
- chooses a random page index based on the total number of posts and posts per page
- obtains all posts for the given page
- chooses a random post from the page
- returns a summary of the search
 */
interface SearchUseCase {
    suspend operator fun invoke(
        query: String,
        totalPosts: Int? = null,
        searchHistory: Map<Int, List<Int>>,
    ) : Result

    sealed class Result {
        object Empty : Result()
        object Exhausted : Result()
        data class Valid(
            val query: String,
            val totalPosts: Int,
            val post: Post,
            val postPage: Int,
            val postIndexOnPage: Int,
            val postPageSize: Int
        ) : Result()
    }
}