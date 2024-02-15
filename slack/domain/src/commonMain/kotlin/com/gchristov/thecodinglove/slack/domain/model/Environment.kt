package com.gchristov.thecodinglove.slack.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
    val apiUrl: String,
    val slackRequestVerification: Boolean,
    val slackSlashCommandPubSubTopic: String,
    val slackInteractivityPubSubTopic: String,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "-port not specified." }.first().toInt(),
                apiUrl = requireNotNull(this["-apiUrl"]) { "-apiUrl url not specified." }.first(),
                slackRequestVerification = requireNotNull(this["-slackRequestVerification"]) { "-slackRequestVerification not specified." }.first()
                    .toBoolean(),
                slackSlashCommandPubSubTopic = requireNotNull(this["-slackSlashCommandPubSubTopic"]) { "-slackSlashCommandPubSubTopic url not specified." }.first(),
                slackInteractivityPubSubTopic = requireNotNull(this["-slackInteractivityPubSubTopic"]) { "-slackInteractivityPubSubTopic url not specified." }.first(),
            )
        }
    }
}