package com.gchristov.thecodinglove.slackweb.service

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.CommonKotlinModule
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.di.DiGraph
import com.gchristov.thecodinglove.common.kotlin.di.inject
import com.gchristov.thecodinglove.common.kotlin.di.registerModules
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.CommonMonitoringModule
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.CommonNetworkModule
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.network.http.StaticFileHttpHandler
import com.gchristov.thecodinglove.slackweb.adapter.SlackWebAdapterModule
import com.gchristov.thecodinglove.slackweb.adapter.http.SlackAuthRedirectHttpHandler
import com.gchristov.thecodinglove.slackweb.domain.SlackWebDomainModule
import com.gchristov.thecodinglove.slackweb.domain.model.Environment

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SlackWebService"

    setupDi()
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

private fun setupDi(): Either<Throwable, Unit> {
    DiGraph.registerModules(
        listOf(
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonMonitoringModule.module,
            SlackWebAdapterModule.module,
            SlackWebDomainModule.module,
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
    val staticWebsiteRoot = ""
    val handlers = listOf(
        DiGraph.inject<SlackAuthRedirectHttpHandler>(),
        // Link this last so that bespoke handlers are correctly registered
        StaticFileHttpHandler("$staticWebsiteRoot/index.html"),
    )
    val service = DiGraph.inject<HttpService>()
    return service.initialise(
        handlers = handlers,
        port = port,
        staticWebsiteRoot = staticWebsiteRoot,
    ).flatMap { Either.Right(service) }
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> = service
    .start()
    .flatMap { Either.Right(Unit) }