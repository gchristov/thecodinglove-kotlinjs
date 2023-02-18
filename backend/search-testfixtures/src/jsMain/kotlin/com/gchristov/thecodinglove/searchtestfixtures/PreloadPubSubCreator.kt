package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage

object PreloadPubSubCreator {
    fun defaultMessage() = PreloadPubSubMessage(
        searchSessionId = "session_123"
    )
}