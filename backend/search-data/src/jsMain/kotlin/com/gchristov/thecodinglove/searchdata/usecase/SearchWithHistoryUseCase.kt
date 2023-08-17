package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.Post
import com.gchristov.thecodinglove.searchdata.domain.SearchConfig
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

interface SearchWithHistoryUseCase {
    suspend operator fun invoke(
        query: String,
        totalPosts: Int? = null,
        searchHistory: Map<Int, List<Int>>,
    ) : Either<SearchError, Result>

    data class Result(
        val query: String,
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    )
}

class RealSearchWithHistoryUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchConfig: SearchConfig
) : SearchWithHistoryUseCase {
    override suspend operator fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
    ): Either<SearchError, SearchWithHistoryUseCase.Result> = withContext(dispatcher) {
        // Find total number of posts
        (totalPosts?.let { Either.Right(it) } ?: searchRepository.getTotalPosts(query))
            .mapLeft { SearchError.Empty }
            .flatMap { totalResults ->
                if (totalResults <= 0) {
                    return@withContext Either.Left(SearchError.Empty)
                }
                // Generate random page number
                Random.nextRandomPage(
                    totalResults = totalResults,
                    resultsPerPage = searchConfig.postsPerPage,
                    exclusions = searchHistory.getExcludedPages()
                )
                    .mapLeft { it.toSearchError() }
                    .flatMap { postPage ->
                        // Get all posts on the random page
                        searchRepository.search(
                            page = postPage,
                            query = query
                        )
                            .mapLeft { SearchError.Empty }
                            .flatMap { searchResults ->
                                if (searchResults.isEmpty()) {
                                    return@withContext Either.Left(SearchError.Empty)
                                }
                                // Pick a post randomly from the page
                                Random.nextRandomPostIndex(
                                    posts = searchResults,
                                    exclusions = searchHistory.getExcludedPostIndexes(postPage)
                                )
                                    .mapLeft { it.toSearchError() }
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

private fun RangeError.toSearchError() = when (this) {
    is RangeError.Empty -> SearchError.Empty
    is RangeError.Exhausted -> SearchError.Exhausted
}