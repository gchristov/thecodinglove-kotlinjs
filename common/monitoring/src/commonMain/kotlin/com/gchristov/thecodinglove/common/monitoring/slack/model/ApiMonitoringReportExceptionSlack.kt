package com.gchristov.thecodinglove.common.monitoring.slack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiMonitoringReportExceptionSlack(
    @SerialName("message") val message: String,
    @SerialName("stacktrace") val stacktrace: String,
)