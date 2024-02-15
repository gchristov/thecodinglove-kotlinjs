package com.gchristov.thecodinglove.search.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
    val apiUrl: String,
    val preloadSearchPubSubTopic: String,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "-port not specified." }.first().toInt(),
                apiUrl = requireNotNull(this["-apiUrl"]) { "-apiUrl url not specified." }.first(),
                preloadSearchPubSubTopic = requireNotNull(this["-preloadSearchPubSubTopic"]) { "-preloadSearchPubSubTopic not specified." }.first(),
            )
        }
    }
}