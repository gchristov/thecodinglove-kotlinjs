package com.gchristov.thecodinglove.slackweb.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "-port not specified." }.first().toInt(),
            )
        }
    }
}