package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservicedata.CommonServiceDataModule
import com.gchristov.thecodinglove.htmlparsedata.HtmlParseDataModule
import com.gchristov.thecodinglove.kmpcommonfirebase.KmpCommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonkotlin.KmpCommonKotlinModule
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.kmpcommonkotlin.di.registerModules
import com.gchristov.thecodinglove.kmpcommonnetwork.KmpCommonNetworkModule
import com.gchristov.thecodinglove.search.PreloadSearchPubSubService
import com.gchristov.thecodinglove.search.SearchApiService
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.SearchDataModule
import com.gchristov.thecodinglove.slack.SlackModule
import com.gchristov.thecodinglove.slack.auth.SlackAuthApiService
import com.gchristov.thecodinglove.slack.event.SlackEventApiService
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityApiService
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandApiService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubService
import com.gchristov.thecodinglove.slackdata.SlackDataModule

fun main() {
    setupDi()
    setupServices()
}

private fun setupDi() {
    // Add all modules that should participate in dependency injection for this app
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
    DiGraph.inject<SearchApiService>().register()
    DiGraph.inject<PreloadSearchPubSubService>().register()
    DiGraph.inject<SlackSlashCommandApiService>().register()
    DiGraph.inject<SlackSlashCommandPubSubService>().register()
    DiGraph.inject<SlackInteractivityApiService>().register()
    DiGraph.inject<SlackInteractivityPubSubService>().register()
    DiGraph.inject<SlackAuthApiService>().register()
    DiGraph.inject<SlackEventApiService>().register()
}