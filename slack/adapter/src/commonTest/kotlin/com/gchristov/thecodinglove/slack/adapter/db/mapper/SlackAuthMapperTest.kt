package com.gchristov.thecodinglove.slack.adapter.db.mapper

import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.slack.adapter.db.DbSlackAuthToken
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackAuthMapperTest {
    @Test
    fun domainTokenMappedToDbToken() {
        assertEquals(
            expected = DbSlackAuthToken(
                id = "U123",
                scope = "chat:write",
                token = "xoxp-token",
                teamId = "T456",
                teamName = "Test Team",
            ),
            actual = TestSlackAuthToken.toAuthToken(),
        )
    }

    @Test
    fun dbTokenMappedToDomainToken() {
        assertEquals(
            expected = TestSlackAuthToken,
            actual = TestDbSlackAuthToken.toAuthToken(),
        )
    }

    @Test
    fun roundTripPreservesAllFields() {
        assertEquals(
            expected = TestSlackAuthToken,
            actual = TestSlackAuthToken.toAuthToken().toAuthToken(),
        )
    }
}

private val TestSlackAuthToken = SlackAuthToken(
    id = "U123",
    scope = "chat:write",
    token = "xoxp-token",
    teamId = "T456",
    teamName = "Test Team",
)

private val TestDbSlackAuthToken = DbSlackAuthToken(
    id = "U123",
    scope = "chat:write",
    token = "xoxp-token",
    teamId = "T456",
    teamName = "Test Team",
)
