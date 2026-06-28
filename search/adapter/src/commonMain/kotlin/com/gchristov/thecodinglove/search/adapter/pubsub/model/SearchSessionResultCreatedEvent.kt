package com.gchristov.thecodinglove.search.adapter.pubsub.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchSessionResultCreatedEvent(
    val searchSessionId: String
)
