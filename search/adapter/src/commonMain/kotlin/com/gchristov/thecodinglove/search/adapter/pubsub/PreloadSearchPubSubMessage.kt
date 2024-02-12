package com.gchristov.thecodinglove.search.adapter.pubsub

import kotlinx.serialization.Serializable

@Serializable
data class PreloadSearchPubSubMessage(
    val searchSessionId: String
)