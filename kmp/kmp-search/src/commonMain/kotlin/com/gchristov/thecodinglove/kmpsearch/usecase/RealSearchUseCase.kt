package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpsearch.*
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

internal class RealSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository
) : SearchUseCase {
    override suspend operator fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
        resultsPerPage: Int
    ): SearchUseCase.Result = withContext(dispatcher) {
        val totalResults = totalPosts ?: searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchUseCase.Result.Empty
        }
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        when (randomPostPage) {
            is RandomResult.Exhausted -> SearchUseCase.Result.Exhausted
            is RandomResult.Invalid -> SearchUseCase.Result.Empty
            is RandomResult.Valid -> {
                val searchResults = searchRepository.search(
                    page = randomPostPage.number,
                    query = query
                )
                if (searchResults.isEmpty()) {
                    return@withContext SearchUseCase.Result.Empty
                }
                val randomPostIndexOnPage = Random.nextRandomPostIndex(
                    posts = searchResults,
                    exclusions = searchHistory.getExcludedPostIndexes(randomPostPage.number)
                )
                when (randomPostIndexOnPage) {
                    is RandomResult.Exhausted -> SearchUseCase.Result.Exhausted
                    is RandomResult.Invalid -> SearchUseCase.Result.Empty
                    is RandomResult.Valid -> SearchUseCase.Result.Valid(
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
}