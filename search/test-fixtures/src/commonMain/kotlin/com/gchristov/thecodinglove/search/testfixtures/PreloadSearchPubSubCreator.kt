package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.proto.pubsub.PubSubPreloadSearchMessage

object PreloadSearchPubSubCreator {
    fun defaultMessage() = PubSubPreloadSearchMessage(
        searchSessionId = "session_123"
    )
}