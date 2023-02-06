package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FakePreloadPubSubMessage(message: PreloadPubSubMessage?) : PubSubMessage {
    override val json: String? = message?.let { Json.encodeToString(it) }
}