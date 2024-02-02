package com.gchristov.thecodinglove.appservice

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.CommonFirebaseModule
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
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
import com.gchristov.thecodinglove.common.pubsub.CommonPubSubModule
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.search.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.SearchHttpHandler
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackAuthHttpHandler
import com.gchristov.thecodinglove.slack.SlackEventHttpHandler
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityHttpHandler
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandHttpHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slackdata.SlackDataModule

suspend fun main() {
    // Remove the first two default Node arguments
    val args = parseMainArgs(process.argv.slice(2) as Array<String>)
    val port = requireNotNull(args["-port"]) { "Port number not specified" }.first().toInt()

    setupDi()
        .flatMap { setupMonitoring() }
        .flatMap { setupService(port) }
        .flatMap { startService(it) }
        .flatMap { runDatabaseMigrations() }
        .fold(ifLeft = { error ->
            println("Error starting app-service${error.message?.let { ": $it" } ?: ""}")
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
            CommonPubSubModule.module,
            CommonFirebaseModule.module,
            CommonMonitoringModule.module,
            HtmlParseDataModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SlackModule.module,
            SlackDataModule.module,
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
        DiGraph.inject<SlackSlashCommandHttpHandler>(),
        DiGraph.inject<SlackSlashCommandPubSubHandler>(),
        DiGraph.inject<SlackInteractivityHttpHandler>(),
        DiGraph.inject<SlackInteractivityPubSubHandler>(),
        DiGraph.inject<SlackAuthHttpHandler>(),
        DiGraph.inject<SlackEventHttpHandler>(),
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