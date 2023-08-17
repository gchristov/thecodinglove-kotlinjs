package com.gchristov.thecodinglove.searchdata.domain

sealed class SearchError(error: String? = null) : Throwable(error) {
    object Empty : SearchError("No results found")
    object Exhausted : SearchError("Results exhausted")
    object SessionNotFound : SearchError("Session not found")
}