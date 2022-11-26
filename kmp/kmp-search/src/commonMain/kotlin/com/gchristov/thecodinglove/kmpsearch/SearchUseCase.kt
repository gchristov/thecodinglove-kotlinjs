package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

class SearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        searchHistory: SearchHistory,
        resultsPerPage: Int
    ): SearchResult = withContext(dispatcher) {
        val totalResults = searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchResult.Empty
        }

        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        if (randomPostPage == RandomResult.Invalid) {
            return@withContext SearchResult.Empty
        }

        val searchResults = searchRepository.search(
            page = (randomPostPage as RandomResult.Valid).number,
            query = query
        )
        if (searchResults.isEmpty()) {
            return@withContext SearchResult.Empty
        }

        val randomPostIndexOnPage = Random.nextRandomPostIndex(
            posts = searchResults,
            exclusions = searchHistory.getExcludedPostIndexes(randomPostPage.number)
        )
        if (randomPostIndexOnPage == RandomResult.Invalid) {
            return@withContext SearchResult.Empty
        }

        SearchResult.Valid(
            query = query,
            totalPosts = totalResults,
            post = searchResults[(randomPostIndexOnPage as RandomResult.Valid).number],
            postPage = randomPostPage.number,
            postIndexOnPage = randomPostIndexOnPage.number,
            postPageSize = searchResults.size
        )
    }
}

sealed class SearchResult {
    object Empty : SearchResult()
    data class Valid(
        val query: String,
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    ) : SearchResult()
}