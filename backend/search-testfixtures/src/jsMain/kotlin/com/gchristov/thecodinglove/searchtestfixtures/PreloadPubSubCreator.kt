package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage

object PreloadPubSubCreator {
    fun defaultMessage() = PreloadPubSubMessage(
        topic = "topic_123",
        searchSessionId = "session_123"
    )
}