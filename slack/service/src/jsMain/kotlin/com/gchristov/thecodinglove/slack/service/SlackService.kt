package com.gchristov.thecodinglove.slack.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSearchPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSelfDestructMessagePubSubHandler
import com.gchristov.thecodinglove.slack.domain.model.Environment

suspend fun main() {
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SlackService"

    val component = SlackComponent::class.create(environment)
    setupMonitoring(component.monitoringLogWriter)
    val service = setupService(
        slackSlashCommandHttpHandler = component.slackSlashCommandHttpHandler,
        slackSearchPubSubHandler = component.slackSearchPubSubHandler,
        slackInteractivityHttpHandler = component.slackInteractivityHttpHandler,
        slackInteractivityPubSubHandler = component.slackInteractivityPubSubHandler,
        slackAuthHttpHandler = component.slackAuthHttpHandler,
        slackEventHttpHandler = component.slackEventHttpHandler,
        slackSelfDestructMessagePubSubHandler = component.slackSelfDestructMessagePubSubHandler,
        slackStatisticsHttpHandler = component.slackStatisticsHttpHandler,
        httpService = component.httpService,
        port = environment.port,
    ).getOrElse { error ->
        component.log.debug(tag, "Error setting up${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    startService(service).getOrElse { error ->
        component.log.debug(tag, "Error starting${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    component.log.debug(tag, "Started: environment=$environment")
}

private fun setupMonitoring(monitoringLogWriter: MonitoringLogWriter) {
    Logger.addLogWriter(monitoringLogWriter)
}

private suspend fun setupService(
    slackSlashCommandHttpHandler: SlackSlashCommandHttpHandler,
    slackSearchPubSubHandler: SlackSearchPubSubHandler,
    slackInteractivityHttpHandler: SlackInteractivityHttpHandler,
    slackInteractivityPubSubHandler: SlackInteractivityPubSubHandler,
    slackAuthHttpHandler: SlackAuthHttpHandler,
    slackEventHttpHandler: SlackEventHttpHandler,
    slackSelfDestructMessagePubSubHandler: SlackSelfDestructMessagePubSubHandler,
    slackStatisticsHttpHandler: SlackStatisticsHttpHandler,
    httpService: HttpService,
    port: Int,
): Either<Throwable, HttpService> {
    httpService.initialise(
        handlers = listOf(
            slackSlashCommandHttpHandler,
            slackSearchPubSubHandler,
            slackInteractivityHttpHandler,
            slackInteractivityPubSubHandler,
            slackAuthHttpHandler,
            slackEventHttpHandler,
            slackSelfDestructMessagePubSubHandler,
            slackStatisticsHttpHandler,
        ),
        port = port,
    ).getOrElse { return Either.Left(it) }
    return Either.Right(httpService)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}
