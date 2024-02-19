package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiSearchStatistics
import com.gchristov.thecodinglove.search.domain.model.SearchStatistics

internal fun SearchStatistics.toStatistics() = ApiSearchStatistics(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
)