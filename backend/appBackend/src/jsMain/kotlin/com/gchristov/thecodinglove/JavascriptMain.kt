package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservice.HttpHandler
import com.gchristov.thecodinglove.commonservice.HttpRequest
import com.gchristov.thecodinglove.commonservice.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.CommonServiceDataModule
import com.gchristov.thecodinglove.express.ExpressHttpBackendService
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.kmpcommonfirebase.KmpCommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonkotlin.KmpCommonKotlinModule
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.kmpcommonkotlin.di.registerModules
import com.gchristov.thecodinglove.kmpcommonnetwork.KmpCommonNetworkModule
import com.gchristov.thecodinglove.search.SearchHttpHandler
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slackdata.SlackDataModule
import io.ktor.http.*

fun main() {
    setupDi()
    setupServices()
}

private fun setupDi() {
    // Add all modules that should participate in dependency injection
    DiGraph.registerModules(
        listOf(
            KmpCommonKotlinModule.module,
            KmpCommonFirebaseModule.module,
            KmpCommonNetworkModule.module,
            CommonServiceDataModule.module,
            HtmlParseDataModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SlackModule.module,
            SlackDataModule.module,
        )
    )
}

private fun setupServices() {
    val app = ExpressHttpBackendService()
    app.serveStaticContent("web")

    app.registerGetHandler(
        path = "/api/test",
        handler = object : HttpHandler {
            override fun handle(
                request: HttpRequest,
                response: HttpResponse,
            ) {
                response.send("Hello, World!")
            }
        }
    )
    app.registerGetHandler(
        path = "/api/searchJson",
        contentType = ContentType.Application.Json,
        handler = DiGraph.inject<SearchHttpHandler>(),
    )
    app.registerGetHandler(
        path = "/api/searchUrl",
        contentType = ContentType.Application.FormUrlEncoded,
        handler = DiGraph.inject<SearchHttpHandler>(),
    )
    app.registerGetHandler(
        path = "*",
        handler = object : HttpHandler {
            override fun handle(
                request: HttpRequest,
                response: HttpResponse,
            ) {
                response.sendFile(localPath = "web/index.html")
            }
        }
    )

    val port = 8080
    println("Starting server: port=$port")
    app.start(port)
    println("Server started")
//    DiGraph.inject<SearchApiService>().register()
//    DiGraph.inject<PreloadSearchPubSubService>().register()
//    DiGraph.inject<SlackSlashCommandApiService>().register()
//    DiGraph.inject<SlackSlashCommandPubSubService>().register()
//    DiGraph.inject<SlackInteractivityApiService>().register()
//    DiGraph.inject<SlackInteractivityPubSubService>().register()
//    DiGraph.inject<SlackAuthApiService>().register()
//    DiGraph.inject<SlackEventApiService>().register()
}