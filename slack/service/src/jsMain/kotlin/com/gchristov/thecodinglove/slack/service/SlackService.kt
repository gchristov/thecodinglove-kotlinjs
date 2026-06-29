package com.gchristov.thecodinglove.slack.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsModule
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.kotlin.CommonKotlinModule
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.di.DiGraph
import com.gchristov.thecodinglove.common.kotlin.di.inject
import com.gchristov.thecodinglove.common.kotlin.di.registerModules
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringModule
import com.gchristov.thecodinglove.common.slack.CommonSlackModule
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkModule
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubModule
import com.gchristov.thecodinglove.slack.adapter.SlackAdapterModule
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSearchPubSubHandler
import com.gchristov.thecodinglove.slack.domain.SlackDomainModule
import com.gchristov.thecodinglove.slack.domain.model.Environment

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SlackService"

    setupDi(environment)
    setupMonitoring()
    val service = setupService(environment.port).getOrElse { error ->
        DiGraph.inject<Logger>().debug(tag, "Error setting up${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    startService(service).getOrElse { error ->
        DiGraph.inject<Logger>().debug(tag, "Error starting${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    DiGraph.inject<Logger>().debug(tag, "Started: environment=$environment")
}

private fun setupDi(environment: Environment) {
    DiGraph.registerModules(
        listOf(
            CommonAnalyticsModule.module,
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonPubSubModule.module,
            CommonSlackModule.module,
            CommonMonitoringModule.module,
            CommonFirebaseModule.module,
            SlackDomainModule.module,
            SlackAdapterModule.module,
            SlackServiceModule(environment).module,
        )
    )
}

private fun setupMonitoring() {
    DiGraph.inject<MonitoringLogWriter>().apply {
        Logger.addLogWriter(this)
    }
}

private suspend fun setupService(port: Int): Either<Throwable, HttpService> {
    val handlers = listOf(
        DiGraph.inject<SlackSlashCommandHttpHandler>(),
        DiGraph.inject<SlackSearchPubSubHandler>(),
        DiGraph.inject<SlackInteractivityHttpHandler>(),
        DiGraph.inject<SlackInteractivityPubSubHandler>(),
        DiGraph.inject<SlackAuthHttpHandler>(),
        DiGraph.inject<SlackEventHttpHandler>(),
        DiGraph.inject<SlackSelfDestructHttpHandler>(),
        DiGraph.inject<SlackStatisticsHttpHandler>(),
    )
    val service = DiGraph.inject<HttpService>()
    service.initialise(handlers = handlers, port = port).getOrElse { return Either.Left(it) }
    return Either.Right(service)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}