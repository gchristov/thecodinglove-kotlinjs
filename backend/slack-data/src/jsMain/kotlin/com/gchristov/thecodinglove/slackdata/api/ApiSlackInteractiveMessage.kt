package com.gchristov.thecodinglove.slackdata.api

import arrow.core.Either
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class ApiSlackInteractivity(
    @SerialName("payload") val payload: String,
)

@Serializable
sealed class ApiSlackInteractivePayload {
    @Serializable
    @SerialName("interactive_message")
    data class ApiInteractiveMessage(
        @SerialName("type") val type: String,
        @SerialName("actions") val actions: List<ApiAction>,
        @SerialName("team") val team: ApiTeam,
        @SerialName("channel") val channel: ApiChannel,
        @SerialName("user") val user: ApiUser,
        @SerialName("response_url") val responseUrl: String,
    ) : ApiSlackInteractivePayload() {
        @Serializable
        data class ApiAction(
            @SerialName("name") val name: String,
            @SerialName("value") val value: String,
        )

        @Serializable
        data class ApiTeam(
            @SerialName("id") val id: String,
            @SerialName("domain") val domain: String,
        )

        @Serializable
        data class ApiChannel(
            @SerialName("id") val id: String,
            @SerialName("name") val name: String,
        )

        @Serializable
        data class ApiUser(
            @SerialName("id") val id: String,
            @SerialName("name") val name: String,
        )
    }
}

fun ApiSlackInteractivity.toPayload(
    jsonSerializer: Json
): Either<Throwable, ApiSlackInteractivePayload?> = try {
    // Payload is encoded as application/x-www-form-urlencoded
    // https://api.slack.com/legacy/message-buttons
    Either.Right(jsonSerializer.decodeFromString<ApiSlackInteractivePayload>(payload))
} catch (error: Throwable) {
    Either.Left(error)
}