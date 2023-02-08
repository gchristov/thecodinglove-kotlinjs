package com.gchristov.thecodinglove.commonservicetestfixtures

import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import kotlin.js.Promise
import kotlin.test.assertEquals

class FakePubSubServiceRegister : PubSubServiceRegister {
    private var invocations = 0

    override fun register(
        topic: String,
        callback: (message: PubSubMessage) -> Promise<Unit>
    ) {
        invocations++
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = invocations
        )
    }
}