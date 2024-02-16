package com.gchristov.thecodinglove.slack.proto.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackReportException(
    @SerialName("message") val message: String,
    @SerialName("stacktrace") val stacktrace: String,
)