package com.gchristov.thecodinglove.search.service

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
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
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubModule
import com.gchristov.thecodinglove.search.adapter.SearchAdapterModule
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.domain.SearchDomainModule
import com.gchristov.thecodinglove.search.domain.model.Environment

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SearchService"

    setupDi(environment = environment)
        .flatMap { setupMonitoring() }
        .flatMap { setupService(environment.port) }
        .flatMap { startService(it) }
        .flatMap { runDatabaseMigrations() }
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
            CommonPubSubModule.module,
            CommonMonitoringModule(environment.apiUrl).module,
            CommonFirebaseModule.module,
            SearchAdapterModule(environment).module,
            SearchDomainModule.module,
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
        DiGraph.inject<SearchHttpHandler>(),
        DiGraph.inject<PreloadSearchPubSubHandler>(),
        DiGraph.inject<SearchStatisticsHttpHandler>(),
        DiGraph.inject<DeleteSearchSessionHttpHandler>(),
        DiGraph.inject<SearchSessionPostHttpHandler>(),
        DiGraph.inject<UpdateSearchSessionStateHttpHandler>(),
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

private suspend fun runDatabaseMigrations(): Either<Throwable, Unit> {
    val migrations = DiGraph.inject<List<FirestoreMigration>>()
    return migrations
        .map { it.invoke() }
        .let { l -> either { l.bindAll() } }
}