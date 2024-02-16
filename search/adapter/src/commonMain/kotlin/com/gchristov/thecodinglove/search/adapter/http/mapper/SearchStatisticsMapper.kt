package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.domain.model.SearchStatistics
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchStatistics

internal fun SearchStatistics.toStatistics() = ApiSearchStatistics(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
)