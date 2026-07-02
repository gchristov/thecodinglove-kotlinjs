package com.gchristov.thecodinglove.slack.adapter.db.mapper

import com.gchristov.thecodinglove.slack.adapter.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackSelfDestructMessageMapperTest {
    @Test
    fun domainMessageMappedToDbMessage() {
        assertEquals(
            expected = TestDbMessage,
            actual = TestDomainMessage.toSelfDestructMessage(),
        )
    }

    @Test
    fun dbMessageMappedToDomainMessage() {
        assertEquals(
            expected = TestDomainMessage,
            actual = TestDbMessage.toSelfDestructMessage(),
        )
    }

    @Test
    fun roundTripPreservesAllFields() {
        assertEquals(
            expected = TestDomainMessage,
            actual = TestDomainMessage.toSelfDestructMessage().toSelfDestructMessage(),
        )
    }
}

private val TestDomainMessage = SlackSentMessage(
    id = "msg_123",
    userId = "user_456",
    searchSessionId = "session_789",
    destroyTimestamp = 1234567890L,
    channelId = "channel_abc",
    messageTs = "1234567890.000100",
)

private val TestDbMessage = DbSlackSelfDestructMessage(
    id = "msg_123",
    userId = "user_456",
    searchSessionId = "session_789",
    destroyTimestamp = 1234567890L,
    channelId = "channel_abc",
    messageTs = "1234567890.000100",
)
