package com.gchristov.thecodinglove.commonservicetestfixtures

import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import kotlin.test.assertEquals

class FakePubSubSender : PubSubSender {
    private var lastTopic: String? = null
    private var lastBody: String? = null
    private var invocations = 0

    override fun sendMessage(
        topic: String,
        body: String
    ) {
        invocations++
        lastTopic = topic
        lastBody = body
    }

    fun assertEquals(
        topic: String?,
        body: String?
    ) {
        assertEquals(
            expected = topic,
            actual = lastTopic
        )
        assertEquals(
            expected = body,
            actual = lastBody
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = invocations
        )
    }
}