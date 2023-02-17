package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ApiSlackMessage {
    @SerialName("text")
    abstract val text: String?

    @SerialName("response_type")
    abstract val responseType: String

    @SerialName("replace_original")
    abstract val replaceOriginal: Boolean

    @SerialName("delete_original")
    abstract val deleteOriginal: Boolean

    @Serializable
    data class ApiProcessing(
        override val text: String,
        override val responseType: String = "ephemeral",
        override val replaceOriginal: Boolean = true,
        override val deleteOriginal: Boolean = false,
    ) : ApiSlackMessage()
}

//@Serializable
//data class ApiAttachment(
//    @SerialName("title")
//    val title: String,
//    @SerialName("title_link")
//    val titleLink: String,
//    @SerialName("text")
//    val text: String,
//    @SerialName("image_url")
//    val imageUrl: String,
//    @SerialName("footer")
//    val footer: String = "Posted using /codinglove",
//    @SerialName("callback_id")
//    // This is needed but unused
//    val callbackId: String = uuid4().toString(),
//    @SerialName("color")
//    val color: String = "#1e1e1e",
//    @SerialName("actions")
//    val actions: List<ApiAction> = emptyList(),
//) {
//    @Serializable
//    data class ApiAction(
//        @SerialName("name")
//        val name: String,
//        @SerialName("text")
//        val text: String,
//        @SerialName("type")
//        val type: String,
//        @SerialName("value")
//        val value: String,
//        @SerialName("url")
//        val url: String,
//        @SerialName("style")
//        val style: String,
//    )
//}
//
//@Serializable
//private data class Old(
//    @SerialName("text")
//    val text: String? = null,
//    @SerialName("user_id")
//    val userId: String? = null,
//    @SerialName("channel")
//    val channelId: String? = null,
//    @SerialName("response_url")
//    val responseUrl: String? = null,
//    @SerialName("response_type")
//    val responseType: String,
//    @SerialName("team")
//    val teamId: String? = null,
//    @SerialName("as_user")
//    val asUser: Boolean = true,
//    @SerialName("replace_original")
//    val replaceOriginal: Boolean = true,
//    @SerialName("delete_original")
//    val deleteOriginal: Boolean = false,
//    @SerialName("attachments")
//    val attachments: List<ApiAttachment> = emptyList(),
//)