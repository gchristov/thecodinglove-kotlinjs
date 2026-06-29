package com.gchristov.thecodinglove.search.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.CommonAnalyticsModule
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
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
import com.gchristov.thecodinglove.search.adapter.SearchAdapterModule
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.adapter.pubsub.SearchPreloadPubSubHandler
import com.gchristov.thecodinglove.search.domain.SearchDomainModule
import com.gchristov.thecodinglove.search.domain.model.Environment

suspend fun main() {
    // Ignore default Node arguments
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SearchService"

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
    runDatabaseMigrations().getOrElse { error ->
        DiGraph.inject<Logger>().debug(tag, "Error run database migrations${error.message?.let { ": $it" } ?: ""}")
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
            SearchAdapterModule.module,
            SearchDomainModule.module,
            SearchServiceModule(environment).module,
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
        DiGraph.inject<SearchHttpHandler>(),
        DiGraph.inject<SearchPreloadPubSubHandler>(),
        DiGraph.inject<SearchStatisticsHttpHandler>(),
        DiGraph.inject<DeleteSearchSessionHttpHandler>(),
        DiGraph.inject<SearchSessionPostHttpHandler>(),
        DiGraph.inject<UpdateSearchSessionStateHttpHandler>(),
    )
    val service = DiGraph.inject<HttpService>()
    service.initialise(handlers = handlers, port = port).getOrElse { return Either.Left(it) }
    return Either.Right(service)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}

private suspend fun runDatabaseMigrations(): Either<Throwable, Unit> {
    val migrations = DiGraph.inject<List<FirestoreMigration>>()
    for (migration in migrations) {
        migration.invoke().getOrElse { return Either.Left(it) }
    }
    return Either.Right(Unit)
}