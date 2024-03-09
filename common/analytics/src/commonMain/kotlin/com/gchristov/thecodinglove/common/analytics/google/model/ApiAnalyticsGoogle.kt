package com.gchristov.thecodinglove.common.analytics.google.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiAnalyticsGoogleRequest(
    @SerialName("client_id") val clientId: String,
    @SerialName("events") val events: List<ApiEvent>?,
) {
    @Serializable
    data class ApiEvent(
        @SerialName("name") val name: String,
        @SerialName("params") val params: Map<String, String>?,
    )
}