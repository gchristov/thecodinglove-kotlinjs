package com.gchristov.thecodinglove.search.domain.model

data class SearchStatistics(
    val messagesSent: Int,
    val activeSearchSessions: Int,
    val messagesSelfDestruct: Int,
)