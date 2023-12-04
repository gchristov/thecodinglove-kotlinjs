package com.gchristov.thecodinglove.searchdata.domain

sealed class SearchError(override val message: String? = null) : Throwable(message) {
    abstract val additionalInfo: String?
    data class Empty(
        override val additionalInfo: String? = null
    ) : SearchError("No results found${additionalInfo?.let { ": $it" } ?: ""}")

    data class Exhausted(
        override val additionalInfo: String? = null
    ) : SearchError("Results exhausted${additionalInfo?.let { ": $it" } ?: ""}")

    data class SessionNotFound(
        override val additionalInfo: String? = null
    ) : SearchError("Session not found${additionalInfo?.let { ": $it" } ?: ""}")
}