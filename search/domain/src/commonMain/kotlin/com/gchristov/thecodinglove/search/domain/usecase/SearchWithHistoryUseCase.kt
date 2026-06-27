package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.search.domain.RangeError
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.getExcludedPages
import com.gchristov.thecodinglove.search.domain.model.getExcludedPostIndexes
import com.gchristov.thecodinglove.search.domain.nextRandomPage
import com.gchristov.thecodinglove.search.domain.nextRandomPostIndex
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

interface SearchWithHistoryUseCase {
    suspend operator fun invoke(dto: Dto): Either<Error, Result>

    data class Result(
        val query: String,
        val totalPosts: Int,
        val post: SearchPost,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    )

    data class Dto(
        val query: String,
        val totalPosts: Int? = null,
        val searchHistory: Map<Int, List<Int>>,
    )

    sealed class Error(override val message: String? = null) : Throwable(message) {
        abstract val additionalInfo: String?

        data class Empty(
            override val additionalInfo: String? = null
        ) : Error("No results found${additionalInfo?.let { ": $it" } ?: ""}")

        data class Exhausted(
            override val additionalInfo: String? = null
        ) : Error("Results exhausted${additionalInfo?.let { ": $it" } ?: ""}")
    }
}

internal class RealSearchWithHistoryUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchConfig: SearchConfig,
) : SearchWithHistoryUseCase {
    override suspend operator fun invoke(
        dto: SearchWithHistoryUseCase.Dto
    ): Either<SearchWithHistoryUseCase.Error, SearchWithHistoryUseCase.Result> = withContext(dispatcher) {
        either {
            // Find total number of posts
            val totalResults = (dto.totalPosts?.let { Either.Right(it) } ?: searchRepository.getTotalPosts(dto.query))
                .mapLeft { SearchWithHistoryUseCase.Error.Empty(additionalInfo = it.message) }
                .bind()
            if (totalResults <= 0) raise(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "query=${dto.query}"))
            // Generate random page number
            val postPage = Random.nextRandomPage(
                totalResults = totalResults,
                resultsPerPage = searchConfig.postsPerPage,
                exclusions = dto.searchHistory.getExcludedPages(),
            ).mapLeft { it.toSearchError(dto.query) }.bind()
            // Get all posts on the random page
            val searchResults = searchRepository.search(
                page = postPage,
                query = dto.query,
            ).mapLeft { SearchWithHistoryUseCase.Error.Empty(additionalInfo = it.message) }.bind()
            if (searchResults.isEmpty()) raise(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "query=${dto.query}"))
            // Pick a post randomly from the page
            val postIndexOnPage = Random.nextRandomPostIndex(
                posts = searchResults,
                exclusions = dto.searchHistory.getExcludedPostIndexes(postPage),
            ).mapLeft { it.toSearchError(dto.query) }.bind()
            SearchWithHistoryUseCase.Result(
                query = dto.query,
                totalPosts = totalResults,
                post = searchResults[postIndexOnPage],
                postPage = postPage,
                postIndexOnPage = postIndexOnPage,
                postPageSize = searchResults.size,
            )
        }
    }
}

private fun RangeError.toSearchError(query: String) = when (this) {
    is RangeError.Empty -> SearchWithHistoryUseCase.Error.Empty(additionalInfo = "no results to randomize")
    is RangeError.Exhausted -> SearchWithHistoryUseCase.Error.Exhausted(additionalInfo = "query=$query")
}