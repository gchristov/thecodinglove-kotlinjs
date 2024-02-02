package com.gchristov.thecodinglove.statistics.domain.ports

import arrow.core.Either

interface StatisticsReportSource {
    suspend fun getTotalSentMessages(): Either<Throwable, Int>

    suspend fun getTotalActiveSearchSessions(): Either<Throwable, Int>

    suspend fun getTotalSelfDestructMessages(): Either<Throwable, Int>

    suspend fun getTotalSlackActiveSelfDestructMessages(): Either<Throwable, Int>

    suspend fun getTotalSlackUsers(): Either<Throwable, Int>

    suspend fun getTotalSlackTeams(): Either<Throwable, Int>
}