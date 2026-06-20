package com.gchristov.thecodinglove.common.slack.api.mapper

import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackAuthResponse
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackAuthMapperTest {
    @Test
    fun userTokenMappedFromAuthedUser() {
        val response = ApiSlackAuthResponse(
            ok = true,
            error = null,
            authedUser = ApiSlackAuthResponse.ApiAuthedUser(
                id = "U123",
                scope = "chat:write",
                accessToken = "xoxp-user-token",
            ),
            team = ApiSlackAuthResponse.ApiTeam(
                id = "T456",
                name = "Test Team",
            ),
            scope = null,
            accessToken = null,
            botUserId = null,
        )
        assertEquals(
            expected = SlackAuthToken(
                id = "U123",
                scope = "chat:write",
                token = "xoxp-user-token",
                teamId = "T456",
                teamName = "Test Team",
            ),
            actual = response.toAuthToken(),
        )
    }

    @Test
    fun botTokenMappedFromTopLevelFields() {
        val response = ApiSlackAuthResponse(
            ok = true,
            error = null,
            authedUser = null,
            team = ApiSlackAuthResponse.ApiTeam(
                id = "T456",
                name = "Test Team",
            ),
            scope = "chat:write,channels:read",
            accessToken = "xoxb-bot-token",
            botUserId = "B789",
        )
        assertEquals(
            expected = SlackAuthToken(
                id = "B789",
                scope = "chat:write,channels:read",
                token = "xoxb-bot-token",
                teamId = "T456",
                teamName = "Test Team",
            ),
            actual = response.toAuthToken(),
        )
    }
}
