package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservicedata.CommonServiceDataModule
import com.gchristov.thecodinglove.express.ExpressBackendService
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.kmpcommonfirebase.KmpCommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonkotlin.KmpCommonKotlinModule
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.registerModules
import com.gchristov.thecodinglove.kmpcommonnetwork.KmpCommonNetworkModule
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slackdata.SlackDataModule

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
    val app = ExpressBackendService()
    app.serveStaticContent("web")
    app.get("/api/test") { _, res ->
        res.send("Hello, World!")
    }
    app.get("*") { _, res ->
        res.sendFile(localPath = "web/index.html")
    }
    app.startServer(8080)
//    DiGraph.inject<SearchApiService>().register()
//    DiGraph.inject<PreloadSearchPubSubService>().register()
//    DiGraph.inject<SlackSlashCommandApiService>().register()
//    DiGraph.inject<SlackSlashCommandPubSubService>().register()
//    DiGraph.inject<SlackInteractivityApiService>().register()
//    DiGraph.inject<SlackInteractivityPubSubService>().register()
//    DiGraph.inject<SlackAuthApiService>().register()
//    DiGraph.inject<SlackEventApiService>().register()
}