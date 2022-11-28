package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 Use-case to search for a random post, given a search session. This use-case:
 - obtains the total results for the given query, if not provided
 - chooses a random page index based on the total number of posts and posts per page
 - obtains all posts for the given page
 - chooses a random post from the page
 - returns a summary of the search
 */
class SearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        totalPosts: Int? = null,
        searchHistory: Map<Int, List<Int>>,
        resultsPerPage: Int
    ): Result = withContext(dispatcher) {
        val totalResults = totalPosts ?: searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext Result.Empty
        }
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        when (randomPostPage) {
            is RandomResult.Exhausted -> Result.Exhausted
            is RandomResult.Invalid -> Result.Empty
            is RandomResult.Valid -> {
                val searchResults = searchRepository.search(
                    page = randomPostPage.number,
                    query = query
                )
                if (searchResults.isEmpty()) {
                    return@withContext Result.Empty
                }
                val randomPostIndexOnPage = Random.nextRandomPostIndex(
                    posts = searchResults,
                    exclusions = searchHistory.getExcludedPostIndexes(randomPostPage.number)
                )
                when (randomPostIndexOnPage) {
                    is RandomResult.Exhausted -> Result.Exhausted
                    is RandomResult.Invalid -> Result.Empty
                    is RandomResult.Valid -> Result.Valid(
                        query = query,
                        totalPosts = totalResults,
                        post = searchResults[randomPostIndexOnPage.number],
                        postPage = randomPostPage.number,
                        postIndexOnPage = randomPostIndexOnPage.number,
                        postPageSize = searchResults.size
                    )
                }
            }
        }
    }

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