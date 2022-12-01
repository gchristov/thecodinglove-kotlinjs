package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpsearch.*
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchConfig
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

internal class RealSearchWithHistoryUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchConfig: SearchConfig
) : SearchWithHistoryUseCase {
    override suspend operator fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
    ): SearchWithHistoryUseCase.Result = withContext(dispatcher) {
        val totalResults = totalPosts ?: searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchWithHistoryUseCase.Result.Empty
        }
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = searchConfig.postsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        when (randomPostPage) {
            is RandomResult.Exhausted -> SearchWithHistoryUseCase.Result.Exhausted
            is RandomResult.Invalid -> SearchWithHistoryUseCase.Result.Empty
            is RandomResult.Valid -> {
                val searchResults = searchRepository.search(
                    page = randomPostPage.number,
                    query = query
                )
                if (searchResults.isEmpty()) {
                    return@withContext SearchWithHistoryUseCase.Result.Empty
                }
                val randomPostIndexOnPage = Random.nextRandomPostIndex(
                    posts = searchResults,
                    exclusions = searchHistory.getExcludedPostIndexes(randomPostPage.number)
                )
                when (randomPostIndexOnPage) {
                    is RandomResult.Exhausted -> SearchWithHistoryUseCase.Result.Exhausted
                    is RandomResult.Invalid -> SearchWithHistoryUseCase.Result.Empty
                    is RandomResult.Valid -> SearchWithHistoryUseCase.Result.Valid(
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