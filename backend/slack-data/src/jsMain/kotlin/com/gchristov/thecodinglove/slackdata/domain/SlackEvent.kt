package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackEvent

sealed class SlackEvent {
    data class UrlVerification(
        val challenge: String,
    ) : SlackEvent()

    data class Callback(
        val event: Event
    ) : SlackEvent() {
        sealed class Event {
            data class TokensRevoked(
                val tokens: Tokens,
            ) : Event() {
                data class Tokens(
                    val oAuth: List<String>?,
                    val bot: List<String>?,
                )
            }
        }
    }
}

fun ApiSlackEvent.toEvent(): SlackEvent = when (this) {
    is ApiSlackEvent.ApiUrlVerification -> SlackEvent.UrlVerification(challenge)
    is ApiSlackEvent.ApiCallback -> this.toCallback()
}

private fun ApiSlackEvent.ApiCallback.toCallback(): SlackEvent = when (event) {
    is ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked -> SlackEvent.Callback(
        event = SlackEvent.Callback.Event.TokensRevoked(
            tokens = SlackEvent.Callback.Event.TokensRevoked.Tokens(
                oAuth = event.tokens.oAuth,
                bot = event.tokens.bot,
            )
        )
    )
}