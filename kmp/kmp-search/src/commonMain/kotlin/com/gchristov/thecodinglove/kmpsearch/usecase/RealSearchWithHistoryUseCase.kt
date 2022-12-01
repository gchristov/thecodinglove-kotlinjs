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
        val totalResults = totalPosts ?: searchRepository.getTotalPosts(query).fold(
            ifLeft = { exception ->
                exception.printStackTrace()
                0
            },
            ifRight = { it }
        )
        if (totalResults <= 0) {
            return@withContext SearchWithHistoryUseCase.Result.Empty
        }
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = searchConfig.postsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        randomPostPage.fold(
            ifLeft = { randomError ->
                when (randomError) {
                    is RandomError.Exhausted -> SearchWithHistoryUseCase.Result.Exhausted
                    is RandomError.Invalid -> SearchWithHistoryUseCase.Result.Empty
                }
            },
            ifRight = { postPage ->
                val searchResults = searchRepository.search(
                    page = postPage,
                    query = query
                ).fold(
                    ifLeft = { exception ->
                        exception.printStackTrace()
                        emptyList()
                    },
                    ifRight = { it }
                )
                if (searchResults.isEmpty()) {
                    return@withContext SearchWithHistoryUseCase.Result.Empty
                }
                val randomPostIndexOnPage = Random.nextRandomPostIndex(
                    posts = searchResults,
                    exclusions = searchHistory.getExcludedPostIndexes(postPage)
                )
                randomPostIndexOnPage.fold(
                    ifLeft = { randomError ->
                        when (randomError) {
                            is RandomError.Exhausted -> SearchWithHistoryUseCase.Result.Exhausted
                            is RandomError.Invalid -> SearchWithHistoryUseCase.Result.Empty
                        }
                    },
                    ifRight = { postIndexOnPage ->
                        SearchWithHistoryUseCase.Result.Valid(
                            query = query,
                            totalPosts = totalResults,
                            post = searchResults[postIndexOnPage],
                            postPage = postPage,
                            postIndexOnPage = postIndexOnPage,
                            postPageSize = searchResults.size
                        )
                    }
                )
            }
        )
    }
}