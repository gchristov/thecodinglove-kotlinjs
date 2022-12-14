package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservice.*
import com.gchristov.thecodinglove.htmlparse.HtmlParseModule
import com.gchristov.thecodinglove.kmpcommondi.CommonDiModule
import com.gchristov.thecodinglove.kmpcommondi.DiGraph
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.kmpcommondi.registerModules
import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.kmpcommonnetwork.CommonNetworkModule
import com.gchristov.thecodinglove.search.SearchApiService
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slack.SlackSlashCommandApiService

fun main() {
    setupDi()
    setupServices()
}

private fun setupDi() {
    // Add all modules that should participate in dependency injection for this app
    DiGraph.registerModules(
        listOf(
            CommonDiModule.module,
            CommonFirebaseModule.module,
            CommonNetworkModule.module,
            HtmlParseModule.module,
            SearchModule.module,
            SearchDataModule.module,
            SlackModule.module,
        )
    )
}

private fun setupServices() {
    DiGraph.inject<SearchApiService>().register()
    DiGraph.inject<SlackSlashCommandApiService>().register()
    PreloadTriggerService().register()
    PreloadPubSubService().register()
}

private class PreloadTriggerService : ApiService() {
    override fun register() {
        exports.pubsubsend = registerForApiCallbacks()
    }

    override suspend fun handleRequest(request: ApiRequest, response: ApiResponse) {
        PubSub("thecodinglove-4ab53").topic("trigger").publish(Buffer.from("HELLO!"))
        response.send("OK")
    }
}

private class PreloadPubSubService : PubSubService() {
    override fun register() {
        exports.pubsubreceive = registerForPubSubCallbacks("trigger")
    }

    override suspend fun handleMessage(message: PubSubMessage) {
        println("RECEIVED MESSAGE $message")
    }
}