package com.gchristov.thecodinglove.kmpsearchdata.usecase

/**
Use-case to preload the next search search result for faster API responses. This use-case:
- obtains a search session
- performs a normal load of a post based on the current session's search history
- persists the preloaded post in the search session
 */
interface PreloadSearchResultUseCase {
    suspend operator fun invoke(searchSessionId: String) : Result

    sealed class Result {
        object Success : Result()
        object SessionNotFound : Result()
        object Empty : Result()
    }
}