package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.searchdata.domain.PreloadSearchPubSubMessage

object PreloadSearchPubSubCreator {
    fun defaultMessage() = PreloadSearchPubSubMessage(
        searchSessionId = "session_123"
    )
}