package com.gchristov.thecodinglove.searchdata.model

import kotlinx.serialization.Serializable

@Serializable
data class PreloadSearchPubSubMessage(
    val searchSessionId: String
)

const val PreloadSearchPubSubTopic = "preload_search"