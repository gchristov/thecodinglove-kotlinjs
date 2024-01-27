package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackDeleteMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)

@Serializable
data class ApiSlackDeleteMessage(
    @SerialName("channel") val channelId: String,
    @SerialName("ts") val messageTs: String,
)