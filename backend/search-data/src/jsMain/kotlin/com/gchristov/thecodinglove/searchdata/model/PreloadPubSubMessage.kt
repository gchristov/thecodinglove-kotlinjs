package com.gchristov.thecodinglove.searchdata.model

import kotlinx.serialization.Serializable

@Serializable
data class PreloadPubSubMessage(
    val topic: String,
    val searchSessionId: String
)