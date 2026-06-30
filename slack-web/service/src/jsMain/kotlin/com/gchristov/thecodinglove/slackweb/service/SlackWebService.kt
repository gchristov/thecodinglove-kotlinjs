package com.gchristov.thecodinglove.slackweb.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.network.http.StaticFileHttpHandler
import com.gchristov.thecodinglove.slackweb.adapter.http.SlackAuthRedirectHttpHandler
import com.gchristov.thecodinglove.slackweb.domain.model.Environment

suspend fun main() {
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SlackWebService"

    val component = SlackWebComponent::class.create()
    setupMonitoring(component.monitoringLogWriter)
    val service = setupService(component.slackAuthRedirectHttpHandler, component.httpService, environment.port).getOrElse { error ->
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
    slackAuthRedirectHttpHandler: SlackAuthRedirectHttpHandler,
    httpService: HttpService,
    port: Int,
): Either<Throwable, HttpService> {
    val staticWebsiteRoot = ""
    httpService.initialise(
        handlers = listOf(
            slackAuthRedirectHttpHandler,
            // Link this last so that bespoke handlers are correctly registered
            StaticFileHttpHandler("$staticWebsiteRoot/index.html"),
        ),
        port = port,
        staticWebsiteRoot = staticWebsiteRoot,
    ).getOrElse { return Either.Left(it) }
    return Either.Right(httpService)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}
