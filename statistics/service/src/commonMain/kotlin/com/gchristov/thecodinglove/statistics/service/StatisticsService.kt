package com.gchristov.thecodinglove.statistics.service

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.kotlin.CommonKotlinModule
import com.gchristov.thecodinglove.common.kotlin.di.DiGraph
import com.gchristov.thecodinglove.common.kotlin.di.inject
import com.gchristov.thecodinglove.common.kotlin.di.registerModules
import com.gchristov.thecodinglove.common.kotlin.parseMainArgs
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringModule
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkModule
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slackdata.SlackDataModule
import com.gchristov.thecodinglove.statistics.adapter.StatisticsAdapterModule
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.core.StatisticsDomainModule

suspend fun main() {
    // Remove the first two default Node arguments
    val args = parseMainArgs(process.argv.slice(2) as Array<String>)
    val port = requireNotNull(args["-port"]) { "Port number not specified" }.first().toInt()

    setupDi()
        .flatMap { setupMonitoring() }
        .flatMap { setupService(port) }
        .flatMap { startService(it) }
        .fold(ifLeft = { error ->
            println("Error starting statistics-service${error.message?.let { ": $it" } ?: ""}")
            error.printStackTrace()
        }, ifRight = {
            // TODO: Add start-up metrics
        })
}

private fun setupDi(): Either<Throwable, Unit> {
    DiGraph.registerModules(
        listOf(
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonMonitoringModule.module,
            CommonFirebaseModule.module,
            HtmlParseDataModule.module,
            SearchDataModule.module,
            SlackDataModule.module,
            StatisticsDomainModule.module,
            StatisticsAdapterModule.module,
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
        DiGraph.inject<StatisticsHttpHandler>(),
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