package com.gchristov.thecodinglove.statisticsservice

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonfirebasedata.CommonFirebaseDataModule
import com.gchristov.thecodinglove.commonkotlin.CommonKotlinModule
import com.gchristov.thecodinglove.commonkotlin.di.DiGraph
import com.gchristov.thecodinglove.commonkotlin.di.inject
import com.gchristov.thecodinglove.commonkotlin.di.registerModules
import com.gchristov.thecodinglove.commonkotlin.parseMainArgs
import com.gchristov.thecodinglove.commonkotlin.process
import com.gchristov.thecodinglove.commonnetwork.CommonNetworkModule
import com.gchristov.thecodinglove.commonservice.CommonServiceModule
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.monitoringdata.MonitoringDataModule
import com.gchristov.thecodinglove.monitoringdata.MonitoringLogWriter
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slackdata.SlackDataModule
import com.gchristov.thecodinglove.statisticsdata.StatisticsDataModule

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
            CommonFirebaseDataModule.module,
            CommonServiceModule.module,
            HtmlParseDataModule.module,
            MonitoringDataModule.module,
            SearchDataModule.module,
            SlackDataModule.module,
            StatisticsModule.module,
            StatisticsDataModule.module,
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