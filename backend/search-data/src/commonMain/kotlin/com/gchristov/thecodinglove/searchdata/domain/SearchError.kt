package com.gchristov.thecodinglove.searchdata.domain

sealed class SearchError(val error: String? = null) : Throwable(error) {
    object Empty : SearchError("No results found")
    object Exhausted : SearchError("Results exhausted")
    data class SessionNotFound(override val message: String? = "Session not found") : SearchError(message)
}