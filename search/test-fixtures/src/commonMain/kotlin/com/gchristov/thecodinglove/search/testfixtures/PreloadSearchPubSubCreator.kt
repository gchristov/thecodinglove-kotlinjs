package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.adapter.pubsub.model.PubSubPreloadSearchMessage

object PreloadSearchPubSubCreator {
    fun defaultMessage() = PubSubPreloadSearchMessage(
        searchSessionId = "session_123"
    )
}