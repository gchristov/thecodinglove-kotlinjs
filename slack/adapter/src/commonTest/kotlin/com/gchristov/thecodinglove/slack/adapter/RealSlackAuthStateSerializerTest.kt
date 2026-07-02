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
    fun selfDestructSecondsNullPreserved() {
        val state = TestAuthState.copy(selfDestructSeconds = null)
        val result = serializer.serialize(state)
        val decoded = result.decodeBase64String()
        val parsed = JsonSerializer.Default.json.decodeFromString<ApiSlackAuthState>(decoded).toAuthState()
        assertEquals(expected = state, actual = parsed)
    }

    @Test
    fun selfDestructSecondsValuePreserved() {
        val state = TestAuthState.copy(selfDestructSeconds = 300L)
        val result = serializer.serialize(state)
        val decoded = result.decodeBase64String()
        val parsed = JsonSerializer.Default.json.decodeFromString<ApiSlackAuthState>(decoded).toAuthState()
        assertEquals(expected = state, actual = parsed)
    }

    @Test
    fun legacySelfDestructMinutesFallsBackToSelfDestructSeconds() {
        // Simulates decoding an already-encoded (pre-deploy) state URL that only has the legacy
        // self_destruct_minutes field, to confirm it still resolves to a usable delay.
        val legacyApiState = ApiSlackAuthState(
            searchSessionId = TestAuthState.searchSessionId,
            channelId = TestAuthState.channelId,
            teamId = TestAuthState.teamId,
            userId = TestAuthState.userId,
            responseUrl = TestAuthState.responseUrl,
            selfDestructMinutes = 5,
            selfDestructSeconds = null,
        )
        val parsed = legacyApiState.toAuthState()
        assertEquals(expected = 300L, actual = parsed.selfDestructSeconds)
    }
}

private val TestAuthState = SlackAuthState(
    searchSessionId = "session_123",
    channelId = "channel_abc",
    teamId = "team_xyz",
    userId = "user_456",
    responseUrl = "https://response.url",
    selfDestructSeconds = null,
)
