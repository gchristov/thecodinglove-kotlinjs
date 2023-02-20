package com.gchristov.thecodinglove.searchdata.model

import kotlinx.serialization.Serializable

@Serializable
data class PreloadPubSubMessage(
    val searchSessionId: String
)