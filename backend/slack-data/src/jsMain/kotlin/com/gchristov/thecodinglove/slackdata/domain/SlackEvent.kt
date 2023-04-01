package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackEvent

sealed class SlackEvent {
    data class UrlVerification(
        val challenge: String,
    ) : SlackEvent()
}

fun ApiSlackEvent.toEvent(): SlackEvent = when (this) {
    is ApiSlackEvent.ApiUrlVerification -> SlackEvent.UrlVerification(challenge)
}