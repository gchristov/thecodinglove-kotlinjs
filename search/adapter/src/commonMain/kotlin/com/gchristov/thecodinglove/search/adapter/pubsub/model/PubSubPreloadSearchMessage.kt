package com.gchristov.thecodinglove.search.adapter.pubsub.model

import kotlinx.serialization.Serializable

@Serializable
data class PubSubPreloadSearchMessage(
    val searchSessionId: String
)