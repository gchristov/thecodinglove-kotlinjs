package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.domain.model.SlackEvent
import com.gchristov.thecodinglove.slack.domain.usecase.SlackRevokeTokensUseCase

internal fun SlackEvent.Callback.Event.TokensRevoked.toSlackRevokeTokensDto() = SlackRevokeTokensUseCase.Dto(
    bot = tokens.bot,
    oAuth = tokens.oAuth,
)