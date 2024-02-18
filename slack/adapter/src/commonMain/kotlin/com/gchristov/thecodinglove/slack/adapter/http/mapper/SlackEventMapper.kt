package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackEvent

internal fun ApiSlackEvent.toEvent(): SlackEvent = when (this) {
    is ApiSlackEvent.ApiUrlVerification -> SlackEvent.UrlVerification(challenge)
    is ApiSlackEvent.ApiCallback -> this.toCallback()
}

private fun ApiSlackEvent.ApiCallback.toCallback(): SlackEvent = when (event) {
    is ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked -> SlackEvent.Callback(
        teamId = teamId,
        event = SlackEvent.Callback.Event.TokensRevoked(
            tokens = SlackEvent.Callback.Event.TokensRevoked.Tokens(
                oAuth = event.tokens.oAuth,
                bot = event.tokens.bot,
            )
        )
    )

    is ApiSlackEvent.ApiCallback.ApiEvent.ApiAppUninstalled -> SlackEvent.Callback(
        teamId = teamId,
        event = SlackEvent.Callback.Event.AppUninstalled
    )
}