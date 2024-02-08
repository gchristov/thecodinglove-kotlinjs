package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface SlackStatisticsUseCase {
    suspend operator fun invoke() : Either<Throwable, SlackStatistics>
}

internal class RealSlackStatisticsUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackStatisticsUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(): Either<Throwable, SlackStatistics> = withContext(dispatcher) {
        val activeSelfDestructMessages = async {
            log.debug(tag, "Obtaining all active Slack self-destruct messages")
            slackRepository.getSelfDestructMessages().map { it.size }
        }
        val authTokens = async {
            log.debug(tag, "Obtaining all Slack auth tokens")
            slackRepository.getAuthTokens().map { it.size }
        }
        val teams = async {
            log.debug(tag, "Obtaining all Slack teams")
            slackRepository.getAuthTokens().map { tokens ->
                val teams = mutableSetOf<String>()
                tokens.forEach {
                    teams.add(it.teamId)
                }
                teams.size
            }
        }
        either {
            SlackStatistics(
                activeSelfDestructMessages = activeSelfDestructMessages.await().bind(),
                users = authTokens.await().bind(),
                teams = teams.await().bind(),
            )
        }
    }
}