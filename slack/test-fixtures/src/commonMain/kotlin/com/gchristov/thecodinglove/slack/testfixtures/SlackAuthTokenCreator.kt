package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken

object SlackAuthTokenCreator {
    fun token(id: String = "user_id") = SlackAuthToken(
        id = id,
        scope = "scope",
        token = "token_value",
        teamId = "team_id",
        teamName = "team_name",
    )
}
