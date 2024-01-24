package com.gchristov.thecodinglove.slackdata.domain

data class SlackConfig(
    val signingSecret: String,
    val timestampValidityMinutes: Int,
    val requestVerificationEnabled: Boolean,
    val clientId: String,
    val clientSecret: String,
    val interactivityPubSubTopic: String,
    val slashCommandPubSubTopic: String,
    val monitoringUrl: String,
)