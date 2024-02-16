package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.domain.model.SlackEvent
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackEvent

internal fun ApiSlackEvent.toEvent(): SlackEvent = when (this) {
    is ApiSlackEvent.ApiUrlVerification -> SlackEvent.UrlVerification(challenge)
    is ApiSlackEvent.ApiCallback -> this.toCallback()
}

private fun ApiSlackEvent.ApiCallback.toCallback(): SlackEvent = when (event) {
    is ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked -> SlackEvent.Callback(
        teamId = teamId,
        event = SlackEvent.Callback.Event.TokensRevoked(
            tokens = SlackEvent.Callback.Event.TokensRevoked.Tokens(
                oAuth = (event as ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked).tokens.oAuth,
                bot = (event as ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked).tokens.bot,
            )
        )
    )

    is ApiSlackEvent.ApiCallback.ApiEvent.ApiAppUninstalled -> SlackEvent.Callback(
        teamId = teamId,
        event = SlackEvent.Callback.Event.AppUninstalled
    )
}