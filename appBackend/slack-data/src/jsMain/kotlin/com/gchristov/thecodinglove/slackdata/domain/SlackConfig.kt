package com.gchristov.thecodinglove.slackdata.domain

data class SlackConfig(
    val signingSecret: String,
    val timestampValidityMinutes: Int
)