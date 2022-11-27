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
        totalPosts: Int? = null,
        shuffleHistory: Map<Int, List<Int>>,
        resultsPerPage: Int
    ): SearchResult = withContext(dispatcher) {
        // Process total posts
        val totalResults = totalPosts ?: searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchResult.Empty
        }
        // Randomise next page to search on
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = shuffleHistory.getExcludedPages()
        )
        // TODO: Handle exhausted results
        if (randomPostPage == RandomResult.Invalid || randomPostPage == RandomResult.Exhausted) {
            return@withContext SearchResult.Empty
        }
        // Obtain all results from the random page
        val searchResults = searchRepository.search(
            page = (randomPostPage as RandomResult.Valid).number,
            query = query
        )
        if (searchResults.isEmpty()) {
            return@withContext SearchResult.Empty
        }
        // Randomise next post to return
        val randomPostIndexOnPage = Random.nextRandomPostIndex(
            posts = searchResults,
            exclusions = shuffleHistory.getExcludedPostIndexes(randomPostPage.number)
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