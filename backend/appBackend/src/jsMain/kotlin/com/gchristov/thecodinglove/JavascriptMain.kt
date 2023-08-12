package com.gchristov.thecodinglove

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import com.gchristov.thecodinglove.commonservice.CommonServiceModule
import com.gchristov.thecodinglove.commonservice.http.StaticFileHttpHandler
import com.gchristov.thecodinglove.commonservicedata.CommonServiceDataModule
import com.gchristov.thecodinglove.commonservicedata.http.HttpService
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.kmpcommonfirebase.KmpCommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonkotlin.KmpCommonKotlinModule
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.kmpcommonkotlin.di.registerModules
import com.gchristov.thecodinglove.kmpcommonnetwork.KmpCommonNetworkModule
import com.gchristov.thecodinglove.search.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.SearchHttpHandler
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slack.auth.SlackAuthHttpHandler
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityHttpHandler
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandHttpHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slackdata.SlackDataModule

suspend fun main() {
    setupDi()
        .flatMap { setupServices() }
        .flatMap { startServices(it) }
        .fold(ifLeft = { error ->
            println("Error starting app${error.message?.let { ": $it" } ?: ""}")
            error.printStackTrace()
        }, ifRight = {
            // TODO: Add start-up metrics
        })
}

/**
 * Setup dependency injection with all participating modules
 */
private fun setupDi(): Either<Throwable, Unit> {
    DiGraph.registerModules(
        listOf(
            KmpCommonKotlinModule.module,
            KmpCommonFirebaseModule.module,
            KmpCommonNetworkModule.module,
            CommonServiceModule.module,
            CommonServiceDataModule.module,
            HtmlParseDataModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SlackModule.module,
            SlackDataModule.module,
        )
    )
    return Either.Right(Unit)
}

/**
 * Setup services along with their API and HTML handlers.
 */
private suspend fun setupServices(): Either<Throwable, List<HttpService>> {
    val appService = setupAppService()
    return listOf(appService).sequence()
}

/**
 * The main app service handles the static HTML content as well as the public-facing API.
 */
private suspend fun setupAppService(): Either<Throwable, HttpService> {
    val staticWebsiteRoot = "web"
    val handlers = listOf(
        DiGraph.inject<SearchHttpHandler>(),
        DiGraph.inject<PreloadSearchPubSubHandler>(),
        DiGraph.inject<SlackSlashCommandHttpHandler>(),
        DiGraph.inject<SlackSlashCommandPubSubHandler>(),
        DiGraph.inject<SlackInteractivityHttpHandler>(),
        DiGraph.inject<SlackInteractivityPubSubHandler>(),
        DiGraph.inject<SlackAuthHttpHandler>(),
        // Link this last so that API handlers are correctly registered
        StaticFileHttpHandler("$staticWebsiteRoot/index.html"),
    )
    val service = DiGraph.inject<HttpService>()
    return service.initialise(
        handlers = handlers,
        staticWebsiteRoot = staticWebsiteRoot,
        port = 8080,
    ).flatMap { Either.Right(service) }
}

private suspend fun startServices(services: List<HttpService>): Either<Throwable, Unit> = services
    .map { it.start() }
    .sequence()
    .flatMap { Either.Right(Unit) }
