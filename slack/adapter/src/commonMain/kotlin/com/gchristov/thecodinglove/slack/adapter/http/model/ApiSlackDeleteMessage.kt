package com.gchristov.thecodinglove.slack.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackDeleteMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)

@Serializable
internal data class ApiSlackDeleteMessage(
    @SerialName("channel") val channelId: String,
    @SerialName("ts") val messageTs: String,
)