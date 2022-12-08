package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.slack.SlackModule

fun main() {
    SlackModule.injectSlackSlashCommandService().register()
}