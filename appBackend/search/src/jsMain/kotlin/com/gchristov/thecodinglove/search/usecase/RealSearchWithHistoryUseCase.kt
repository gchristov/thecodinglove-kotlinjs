package com.gchristov.thecodinglove.search.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.search.*
import com.gchristov.thecodinglove.searchdata.SearchException
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.model.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithHistoryUseCase
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
    ): Either<SearchException, SearchWithHistoryUseCase.Result> = withContext(dispatcher) {
        // Find total number of posts
        (totalPosts?.let { Either.Right(it) } ?: searchRepository.getTotalPosts(query))
            .mapLeft { SearchException.Empty }
            .flatMap { totalResults ->
                if (totalResults <= 0) {
                    return@withContext Either.Left(SearchException.Empty)
                }
                // Generate random page number
                Random.nextRandomPage(
                    totalResults = totalResults,
                    resultsPerPage = searchConfig.postsPerPage,
                    exclusions = searchHistory.getExcludedPages()
                )
                    .mapLeft { it.toSearchException() }
                    .flatMap { postPage ->
                        // Get all posts on the random page
                        searchRepository.search(
                            page = postPage,
                            query = query
                        )
                            .mapLeft { SearchException.Empty }
                            .flatMap { searchResults ->
                                if (searchResults.isEmpty()) {
                                    return@withContext Either.Left(SearchException.Empty)
                                }
                                // Pick a post randomly from the page
                                Random.nextRandomPostIndex(
                                    posts = searchResults,
                                    exclusions = searchHistory.getExcludedPostIndexes(postPage)
                                )
                                    .mapLeft { it.toSearchException() }
                                    .map { postIndexOnPage ->
                                        SearchWithHistoryUseCase.Result(
                                            query = query,
                                            totalPosts = totalResults,
                                            post = searchResults[postIndexOnPage],
                                            postPage = postPage,
                                            postIndexOnPage = postIndexOnPage,
                                            postPageSize = searchResults.size
                                        )
                                    }
                            }
                    }
            }
    }
}

private fun RangeException.toSearchException() = when (this) {
    is RangeException.Empty -> SearchException.Empty
    is RangeException.Exhausted -> SearchException.Exhausted
}