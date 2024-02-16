package com.gchristov.thecodinglove.search.proto.pubsub

import kotlinx.serialization.Serializable

@Serializable
data class PubSubPreloadSearchMessage(
    val searchSessionId: String
)