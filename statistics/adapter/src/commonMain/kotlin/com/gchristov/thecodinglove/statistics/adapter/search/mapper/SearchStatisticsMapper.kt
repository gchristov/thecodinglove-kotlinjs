package com.gchristov.thecodinglove.statistics.adapter.search.mapper

import com.gchristov.thecodinglove.statistics.adapter.search.model.ApiSearchStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun ApiSearchStatistics.toStatistics() = StatisticsReport.SearchStatistics(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
)