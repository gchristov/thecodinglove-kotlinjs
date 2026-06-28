package com.gchristov.thecodinglove.slack.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
    val apiUrl: String,
    val slackRequestVerification: Boolean,
    val slackSlashCommandReceivedPubSubTopic: String,
    val slackInteractivityReceivedPubSubTopic: String,
    val slackSlashCommand: String,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "-port not specified." }.first().toInt(),
                apiUrl = requireNotNull(this["-apiUrl"]) { "-apiUrl url not specified." }.first(),
                slackRequestVerification = requireNotNull(this["-slackRequestVerification"]) { "-slackRequestVerification not specified." }.first()
                    .toBoolean(),
                slackSlashCommandReceivedPubSubTopic = requireNotNull(this["-slackSlashCommandReceivedPubSubTopic"]) { "-slackSlashCommandReceivedPubSubTopic url not specified." }.first(),
                slackInteractivityReceivedPubSubTopic = requireNotNull(this["-slackInteractivityReceivedPubSubTopic"]) { "-slackInteractivityReceivedPubSubTopic url not specified." }.first(),
                slackSlashCommand = requireNotNull(this["-slackSlashCommand"]) { "-slackSlashCommand not specified." }.first(),
            )
        }
    }
}