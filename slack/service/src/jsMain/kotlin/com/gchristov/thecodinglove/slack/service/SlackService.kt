package com.gchristov.thecodinglove.slack.service

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.kotlin.CommonKotlinModule
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.di.DiGraph
import com.gchristov.thecodinglove.common.kotlin.di.inject
import com.gchristov.thecodinglove.common.kotlin.di.registerModules
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringModule
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.monitoring.domain.MonitoringEnvironment
import com.gchristov.thecodinglove.common.network.CommonNetworkModule
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubModule
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.adapter.SlackAdapterModule
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slack.domain.SlackDomainModule
import com.gchristov.thecodinglove.slack.domain.model.Environment

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val monitoringEnvironment = MonitoringEnvironment.of(process.argv.slice(2) as Array<String>)
    val tag = "SlackService"

    setupDi(
        monitoringEnvironment = monitoringEnvironment,
    )
        .flatMap { setupMonitoring() }
        .flatMap { setupService(environment.port) }
        .flatMap { startService(it) }
        .fold(ifLeft = { error ->
            val log = DiGraph.inject<Logger>()
            log.debug(tag, "Error starting${error.message?.let { ": $it" } ?: ""}")
            error.printStackTrace()
        }, ifRight = {
            val log = DiGraph.inject<Logger>()
            log.debug(tag, "Started: environment=$environment")
        })
}

private fun setupDi(monitoringEnvironment: MonitoringEnvironment): Either<Throwable, Unit> {
    DiGraph.registerModules(
        listOf(
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonPubSubModule.module,
            CommonMonitoringModule(monitoringEnvironment).module,
            CommonFirebaseModule.module,
            HtmlParseDataModule.module,
            SearchDataModule.module,
            SlackDomainModule.module,
            SlackAdapterModule.module,
        )
    )
    return Either.Right(Unit)
}

private fun setupMonitoring(): Either<Throwable, Unit> {
    DiGraph.inject<MonitoringLogWriter>().apply {
        Logger.addLogWriter(this)
    }
    return Either.Right(Unit)
}

private suspend fun setupService(port: Int): Either<Throwable, HttpService> {
    val handlers = listOf(
        DiGraph.inject<SlackSlashCommandHttpHandler>(),
        DiGraph.inject<SlackSlashCommandPubSubHandler>(),
        DiGraph.inject<SlackInteractivityHttpHandler>(),
        DiGraph.inject<SlackInteractivityPubSubHandler>(),
        DiGraph.inject<SlackAuthHttpHandler>(),
        DiGraph.inject<SlackEventHttpHandler>(),
        DiGraph.inject<SlackSelfDestructHttpHandler>(),
        DiGraph.inject<SlackStatisticsHttpHandler>(),
        DiGraph.inject<SlackReportExceptionHttpHandler>(),
    )
    val service = DiGraph.inject<HttpService>()
    return service.initialise(
        handlers = handlers,
        port = port,
    ).flatMap { Either.Right(service) }
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> = service
    .start()
    .flatMap { Either.Right(Unit) }