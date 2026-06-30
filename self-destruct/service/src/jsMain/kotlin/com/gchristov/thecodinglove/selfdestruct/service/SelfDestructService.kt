package com.gchristov.thecodinglove.selfdestruct.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.selfdestruct.adapter.http.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment

suspend fun main() {
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SelfDestructService"

    val component = SelfDestructComponent::class.create(environment)
    setupMonitoring(component.monitoringLogWriter)
    val service = setupService(component.httpHandler, component.httpService, environment.port).getOrElse { error ->
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
    httpHandler: SelfDestructHttpHandler,
    httpService: HttpService,
    port: Int,
): Either<Throwable, HttpService> {
    httpService.initialise(handlers = listOf(httpHandler), port = port).getOrElse { return Either.Left(it) }
    return Either.Right(httpService)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}
