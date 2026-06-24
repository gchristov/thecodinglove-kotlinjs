package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.slack.domain.model.SlackConfig

object SlackConfigCreator {
    fun slackConfig(
        signingSecret: String = "signing_secret",
        timestampValidityMinutes: Int = 5,
        requestVerificationEnabled: Boolean = false,
        clientId: String = "client_id",
        clientSecret: String = "client_secret",
        interactivityPubSubTopic: String = "interactivity_topic",
        slashCommandPubSubTopic: String = "slash_topic",
    ) = SlackConfig(
        signingSecret = signingSecret,
        timestampValidityMinutes = timestampValidityMinutes,
        requestVerificationEnabled = requestVerificationEnabled,
        clientId = clientId,
        clientSecret = clientSecret,
        interactivityPubSubTopic = interactivityPubSubTopic,
        slashCommandPubSubTopic = slashCommandPubSubTopic,
    )
}
