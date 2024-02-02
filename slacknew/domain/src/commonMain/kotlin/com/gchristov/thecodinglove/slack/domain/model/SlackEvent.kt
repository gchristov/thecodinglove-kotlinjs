package com.gchristov.thecodinglove.slack.domain.model

sealed class SlackEvent {
    data class UrlVerification(
        val challenge: String,
    ) : SlackEvent()

    data class Callback(
        val teamId: String,
        val event: Event
    ) : SlackEvent() {
        sealed class Event {
            data class TokensRevoked(
                val tokens: Tokens,
            ) : Event() {
                data class Tokens(
                    val oAuth: List<String>?,
                    val bot: List<String>?,
                )
            }

            data object AppUninstalled : Event()
        }
    }
}