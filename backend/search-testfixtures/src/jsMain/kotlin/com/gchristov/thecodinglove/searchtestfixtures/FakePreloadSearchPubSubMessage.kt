package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FakePreloadSearchPubSubMessage(message: PreloadSearchPubSubMessage?) : PubSubMessage {
    override val json: String? = message?.let { Json.encodeToString(it) }
}