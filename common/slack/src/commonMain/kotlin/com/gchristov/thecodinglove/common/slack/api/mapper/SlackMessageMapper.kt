package com.gchristov.thecodinglove.common.slack.api.mapper

import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackMessage
import com.gchristov.thecodinglove.common.slack.model.SlackMessage

internal fun SlackMessage.toApiSlackMessage() = ApiSlackMessage(
    text = text,
    userId = userId,
    channelId = channelId,
    responseType = responseType,
    teamId = teamId,
    replaceOriginal = replaceOriginal,
    deleteOriginal = deleteOriginal,
    attachments = attachments?.map { attachment ->
        ApiSlackMessage.ApiAttachment(
            title = attachment.title,
            titleLink = attachment.titleLink,
            text = attachment.text,
            imageUrl = attachment.imageUrl,
            footer = attachment.footer,
            callbackId = attachment.callbackId,
            color = attachment.color,
            actions = attachment.actions.map { action ->
                ApiSlackMessage.ApiAttachment.ApiAction(
                    name = action.name,
                    text = action.text,
                    type = action.type,
                    value = action.value,
                    url = action.url,
                    style = action.style,
                )
            }
        )
    }
)
