package com.gchristov.thecodinglove.slack.adapter.http.model

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiGraph
import com.gchristov.thecodinglove.common.kotlin.di.inject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ApiSlackInteractivity(
    @SerialName("payload") @Serializable(with = PayloadSerializer::class) val payload: ApiSlackInteractivityPayload,
) {
    @Serializable
    sealed class ApiSlackInteractivityPayload {
        @Serializable
        @SerialName("interactive_message")
        data class ApiInteractiveMessage(
            @SerialName("actions") val actions: List<ApiAction>,
            @SerialName("team") val team: ApiTeam,
            @SerialName("channel") val channel: ApiChannel,
            @SerialName("user") val user: ApiUser,
            @SerialName("response_url") val responseUrl: String,
        ) : ApiSlackInteractivityPayload() {
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
}

// Payload is encoded as application/x-www-form-urlencoded
// https://api.slack.com/legacy/message-buttons
private object PayloadSerializer : KSerializer<ApiSlackInteractivity.ApiSlackInteractivityPayload> {
    private val jsonSerializer = DiGraph.inject<JsonSerializer.ExplicitNulls>()
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ApiSlackInteractivity.ApiSlackInteractivityPayload",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): ApiSlackInteractivity.ApiSlackInteractivityPayload =
        jsonSerializer.json.decodeFromString(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: ApiSlackInteractivity.ApiSlackInteractivityPayload
    ) {
        encoder.encodeString(jsonSerializer.json.encodeToString(value))
    }
}