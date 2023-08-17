package com.gchristov.thecodinglove.searchdata.domain

import kotlinx.serialization.Serializable

@Serializable
data class PreloadSearchPubSubMessage(
    val searchSessionId: String
)