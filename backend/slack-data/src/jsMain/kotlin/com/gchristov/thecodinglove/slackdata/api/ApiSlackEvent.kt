package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ApiSlackEvent {
    abstract val type: String

    @Serializable
    @SerialName("url_verification")
    data class ApiUrlVerification(
        @SerialName("type") override val type: String,
        @SerialName("challenge") val challenge: String,
    ) : ApiSlackEvent()
}