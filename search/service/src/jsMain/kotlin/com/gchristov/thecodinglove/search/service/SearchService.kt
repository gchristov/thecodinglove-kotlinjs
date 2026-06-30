package com.gchristov.thecodinglove.search.service

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.process
import com.gchristov.thecodinglove.common.monitoring.MonitoringLogWriter
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.search.adapter.http.*
import com.gchristov.thecodinglove.search.adapter.pubsub.SearchPreloadPubSubHandler
import com.gchristov.thecodinglove.search.domain.model.Environment

suspend fun main() {
    val environment = Environment.of(process.argv.slice(2) as Array<String>)
    val tag = "SearchService"

    val component = SearchComponent::class.create(environment)
    setupMonitoring(component.monitoringLogWriter)
    val service = setupService(
        searchHttpHandler = component.searchHttpHandler,
        searchPreloadPubSubHandler = component.searchPreloadPubSubHandler,
        searchStatisticsHttpHandler = component.searchStatisticsHttpHandler,
        deleteSearchSessionHttpHandler = component.deleteSearchSessionHttpHandler,
        searchSessionPostHttpHandler = component.searchSessionPostHttpHandler,
        updateSearchSessionStateHttpHandler = component.updateSearchSessionStateHttpHandler,
        httpService = component.httpService,
        port = environment.port,
    ).getOrElse { error ->
        component.log.debug(tag, "Error setting up${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    startService(service).getOrElse { error ->
        component.log.debug(tag, "Error starting${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    runDatabaseMigrations(component.firestoreMigrations).getOrElse { error ->
        component.log.debug(tag, "Error run database migrations${error.message?.let { ": $it" } ?: ""}")
        error.printStackTrace()
        return
    }
    component.log.debug(tag, "Started: environment=$environment")
}

private fun setupMonitoring(monitoringLogWriter: MonitoringLogWriter) {
    Logger.addLogWriter(monitoringLogWriter)
}

private suspend fun setupService(
    searchHttpHandler: SearchHttpHandler,
    searchPreloadPubSubHandler: SearchPreloadPubSubHandler,
    searchStatisticsHttpHandler: SearchStatisticsHttpHandler,
    deleteSearchSessionHttpHandler: DeleteSearchSessionHttpHandler,
    searchSessionPostHttpHandler: SearchSessionPostHttpHandler,
    updateSearchSessionStateHttpHandler: UpdateSearchSessionStateHttpHandler,
    httpService: HttpService,
    port: Int,
): Either<Throwable, HttpService> {
    httpService.initialise(
        handlers = listOf(
            searchHttpHandler,
            searchPreloadPubSubHandler,
            searchStatisticsHttpHandler,
            deleteSearchSessionHttpHandler,
            searchSessionPostHttpHandler,
            updateSearchSessionStateHttpHandler,
        ),
        port = port,
    ).getOrElse { return Either.Left(it) }
    return Either.Right(httpService)
}

private suspend fun startService(service: HttpService): Either<Throwable, Unit> {
    service.start().getOrElse { return Either.Left(it) }
    return Either.Right(Unit)
}

private suspend fun runDatabaseMigrations(migrations: List<FirestoreMigration>): Either<Throwable, Unit> {
    for (migration in migrations) {
        migration.invoke().getOrElse { return Either.Left(it) }
    }
    return Either.Right(Unit)
}
