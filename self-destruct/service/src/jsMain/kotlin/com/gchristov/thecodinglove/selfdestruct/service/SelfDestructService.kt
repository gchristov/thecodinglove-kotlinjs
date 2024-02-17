package com.gchristov.thecodinglove.selfdestruct.service

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
import com.gchristov.thecodinglove.selfdestruct.adapter.SelfDestructAdapterModule
import com.gchristov.thecodinglove.selfdestruct.adapter.http.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.domain.SelfDestructDomainModule
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment
import com.gchristov.thecodinglove.slack.proto.SlackProtoModule

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SelfDestructService"

    setupDi(environment = environment)
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

private fun setupDi(environment: Environment): Either<Throwable, Unit> {
    DiGraph.registerModules(
        listOf(
            CommonKotlinModule.module,
            CommonNetworkModule.module,
            CommonMonitoringModule.module,
            SlackProtoModule(environment.apiUrl).module,
            SelfDestructAdapterModule.module,
            SelfDestructDomainModule.module,
            SelfDestructServiceModule(environment).module,
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
        DiGraph.inject<SelfDestructHttpHandler>(),
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