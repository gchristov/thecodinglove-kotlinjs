package com.gchristov.thecodinglove.common.monitoring.slack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiMonitoringSlackPostMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)

@Serializable
internal data class ApiMonitoringSlackMessage(
    @SerialName("text") val text: String?,
    @SerialName("attachments") val attachments: List<ApiAttachment>?,
) {
    @Serializable
    data class ApiAttachment(
        @SerialName("text") val text: String?,
        @SerialName("color") val color: String?,
    )
}