package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent

object PreloadSearchPubSubCreator {
    fun defaultMessage() = SearchSessionResultCreatedEvent(
        searchSessionId = "session_123"
    )
}