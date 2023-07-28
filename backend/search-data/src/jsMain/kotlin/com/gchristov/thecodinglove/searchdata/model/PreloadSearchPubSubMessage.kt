package com.gchristov.thecodinglove.searchdata.model

import kotlinx.serialization.Serializable

@Serializable
data class PreloadSearchPubSubMessage(
    val searchSessionId: String
)

const val PreloadSearchPubSubTopic = "test-4"
const val PreloadSearchPubSubSubscription = "test-sub-4"