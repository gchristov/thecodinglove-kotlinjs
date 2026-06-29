package com.gchristov.thecodinglove.slackweb.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsModule
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
    setupMonitoring()
    val service = setupService(environment.port).getOrElse { error ->
        DiGraph.inject<Logger>().debug(tag, "Error starting${error.message?.let { ": $it" } ?: ""}")
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

private fun setupDi() {
    DiGraph.registerModules(
        listOf(
            CommonAnalyticsModule.module,
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonSlackModule.module,
            CommonMonitoringModule.module,
            SlackWebAdapterModule.module,
            SlackWebDomainModule.module,
        )
    )
}

private fun setupMonitoring() {
    DiGraph.inject<MonitoringLogWriter>().apply {
        Logger.addLogWriter(this)
    }
}

private suspend fun setupService(port: Int): Either<Throwable, HttpService> {
    val staticWebsiteRoot = ""
    val handlers = listOf(
        DiGraph.inject<SlackAuthRedirectHttpHandler>(),
        // Link this last so that bespoke handlers are correctly registered
        StaticFileHttpHandler("$staticWebsiteRoot/index.html"),
    )
    val service = DiGraph.inject<HttpService>()
    service.initialise(handlers = handlers, port = port, staticWebsiteRoot = staticWebsiteRoot)
        .getOrElse { return Either.Left(it) }
    return Either.Right(service)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}