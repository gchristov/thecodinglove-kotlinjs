package com.gchristov.thecodinglove.searchdata.domain

sealed class SearchError(override val message: String? = null) : Throwable(message) {
    data class Empty(
        val additionalInfo: String? = null
    ) : SearchError("No results found${additionalInfo?.let { ": $it" } ?: ""}")

    object Exhausted : SearchError("Results exhausted")
    data class SessionNotFound(
        val additionalInfo: String? = null
    ) : SearchError("Session not found${additionalInfo?.let { ": $it" } ?: ""}")
}