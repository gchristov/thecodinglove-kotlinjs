package com.gchristov.thecodinglove.common.monitoring.slack

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackReportException(
    @SerialName("message") val message: String,
    @SerialName("stacktrace") val stacktrace: String,
)