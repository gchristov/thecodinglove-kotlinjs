package com.gchristov.thecodinglove.statistics.adapter.search.mapper

import com.gchristov.thecodinglove.statistics.adapter.search.model.ApiStatisticsSearch
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun ApiStatisticsSearch.toStatistics() = StatisticsReport.SearchStatistics(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
)