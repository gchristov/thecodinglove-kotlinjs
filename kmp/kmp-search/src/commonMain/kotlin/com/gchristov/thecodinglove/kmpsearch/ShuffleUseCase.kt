package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ShuffleUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchUseCase: SearchUseCase
) {
    suspend operator fun invoke(
        shuffleType: ShuffleType,
        resultsPerPage: Int
    ): ShuffleResult = withContext(dispatcher) {
        val searchSession = getSearchSession(shuffleType)
        val shuffleHistory = searchSession.shuffleHistory ?: mutableMapOf()
        val searchResult = searchUseCase(
            query = searchSession.query,
            totalPosts = searchSession.totalPosts,
            shuffleHistory = shuffleHistory,
            resultsPerPage = resultsPerPage
        )
        when (searchResult) {
            is SearchResult.Empty -> ShuffleResult.Empty
            is SearchResult.Exhausted -> TODO()
            is SearchResult.Valid -> ShuffleResult.Valid(
                query = searchResult.query,
                post = searchResult.post,
                totalPosts = searchResult.totalPosts
            )
        }
    }

    private suspend fun getSearchSession(shuffleType: ShuffleType): SearchSession {
        val newSession = SearchSession(
            id = Random.nextInt().toString(),
            query = shuffleType.query,
            totalPosts = null,
            shuffleHistory = null
        )
        return when (shuffleType) {
            is ShuffleType.NewSearch -> newSession
            is ShuffleType.WithSessionId -> searchRepository.getSearchSession(shuffleType.sessionId)
                ?: newSession
        }
    }
}

sealed class ShuffleResult {
    object Empty : ShuffleResult()
    data class Valid(
        val query: String,
        val post: Post,
        val totalPosts: Int
    ) : ShuffleResult()
}

sealed class ShuffleType {
    abstract val query: String

    data class WithSessionId(
        override val query: String,
        val sessionId: String
    ) : ShuffleType()

    data class NewSearch(
        override val query: String,
    ) : ShuffleType()
}