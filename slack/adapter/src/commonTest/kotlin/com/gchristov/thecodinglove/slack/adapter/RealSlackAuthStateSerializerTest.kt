package com.gchristov.thecodinglove.slack.adapter

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthState
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RealSlackAuthStateSerializerTest {
    private val serializer = RealSlackAuthStateSerializer(JsonSerializer.Default)

    @Test
    fun serializesAuthStateToBase64Json() {
        val result = serializer.serialize(TestAuthState)
        val decoded = result.decodeBase64String()
        val parsed = JsonSerializer.Default.json.decodeFromString<ApiSlackAuthState>(decoded).toAuthState()
        assertEquals(expected = TestAuthState, actual = parsed)
    }

    @Test
    fun selfDestructMinutesNullPreserved() {
        val state = TestAuthState.copy(selfDestructMinutes = null)
        val result = serializer.serialize(state)
        val decoded = result.decodeBase64String()
        val parsed = JsonSerializer.Default.json.decodeFromString<ApiSlackAuthState>(decoded).toAuthState()
        assertEquals(expected = state, actual = parsed)
    }

    @Test
    fun selfDestructMinutesValuePreserved() {
        val state = TestAuthState.copy(selfDestructMinutes = 5)
        val result = serializer.serialize(state)
        val decoded = result.decodeBase64String()
        val parsed = JsonSerializer.Default.json.decodeFromString<ApiSlackAuthState>(decoded).toAuthState()
        assertEquals(expected = state, actual = parsed)
    }
}

private val TestAuthState = SlackAuthState(
    searchSessionId = "session_123",
    channelId = "channel_abc",
    teamId = "team_xyz",
    userId = "user_456",
    responseUrl = "https://response.url",
    selfDestructMinutes = null,
)
