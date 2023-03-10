package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)