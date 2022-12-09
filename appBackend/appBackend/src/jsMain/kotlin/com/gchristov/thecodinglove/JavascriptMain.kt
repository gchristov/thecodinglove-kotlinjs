package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.slack.SlackModule

fun main() {
    SearchModule.injectSearchService().register()
    SlackModule.injectSlackSlashCommandService().register()
}