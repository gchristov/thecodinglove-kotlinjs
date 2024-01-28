package com.gchristov.thecodinglove.appservice

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonfirebasedata.CommonFirebaseDataModule
import com.gchristov.thecodinglove.commonfirebasedata.firestore.FirestoreMigration
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
import com.gchristov.thecodinglove.search.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.SearchHttpHandler
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.selfdestruct.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.SelfDestructModule
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
            CommonFirebaseDataModule.module,
            CommonServiceModule.module,
            HtmlParseDataModule.module,
            MonitoringDataModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SelfDestructModule.module,
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

private suspend fun runDatabaseMigrations(): Either<Throwable, Unit> {
    val migrations = DiGraph.inject<List<FirestoreMigration>>()
    return migrations
        .map { it.invoke() }
        .let { l -> either { l.bindAll() } }
}