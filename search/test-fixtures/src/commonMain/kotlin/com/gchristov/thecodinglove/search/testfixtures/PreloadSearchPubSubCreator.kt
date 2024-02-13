package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubMessage

object PreloadSearchPubSubCreator {
    fun defaultMessage() = PreloadSearchPubSubMessage(
        searchSessionId = "session_123"
    )
}