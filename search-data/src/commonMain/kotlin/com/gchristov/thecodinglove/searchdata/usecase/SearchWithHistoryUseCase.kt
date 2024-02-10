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
    suspend operator fun invoke(dto: Dto): Either<SearchError, Result>

    data class Result(
        val query: String,
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    )

    data class Dto(
        val query: String,
        val totalPosts: Int? = null,
        val searchHistory: Map<Int, List<Int>>,
    )
}

internal class RealSearchWithHistoryUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchConfig: SearchConfig
) : SearchWithHistoryUseCase {
    override suspend operator fun invoke(
        dto: SearchWithHistoryUseCase.Dto
    ): Either<SearchError, SearchWithHistoryUseCase.Result> = withContext(dispatcher) {
        // Find total number of posts
        (dto.totalPosts?.let { Either.Right(it) } ?: searchRepository.getTotalPosts(dto.query))
            .mapLeft { SearchError.Empty(additionalInfo = it.message) }
            .flatMap { totalResults ->
                if (totalResults <= 0) {
                    return@withContext Either.Left(SearchError.Empty(additionalInfo = "query=${dto.query}"))
                }
                // Generate random page number
                Random.nextRandomPage(
                    totalResults = totalResults,
                    resultsPerPage = searchConfig.postsPerPage,
                    exclusions = dto.searchHistory.getExcludedPages()
                )
                    .mapLeft { it.toSearchError(dto.query) }
                    .flatMap { postPage ->
                        // Get all posts on the random page
                        searchRepository.search(
                            page = postPage,
                            query = dto.query
                        )
                            .mapLeft { SearchError.Empty(additionalInfo = it.message) }
                            .flatMap { searchResults ->
                                if (searchResults.isEmpty()) {
                                    return@withContext Either.Left(SearchError.Empty(additionalInfo = "query=${dto.query}"))
                                }
                                // Pick a post randomly from the page
                                Random.nextRandomPostIndex(
                                    posts = searchResults,
                                    exclusions = dto.searchHistory.getExcludedPostIndexes(postPage)
                                )
                                    .mapLeft { it.toSearchError(dto.query) }
                                    .map { postIndexOnPage ->
                                        SearchWithHistoryUseCase.Result(
                                            query = dto.query,
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

private fun RangeError.toSearchError(query: String) = when (this) {
    is RangeError.Empty -> SearchError.Empty(additionalInfo = "could not randomize")
    is RangeError.Exhausted -> SearchError.Exhausted(additionalInfo = "query=$query")
}