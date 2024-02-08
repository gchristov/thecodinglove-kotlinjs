package com.gchristov.thecodinglove.slack.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "Port not specified." }.first().toInt(),
            )
        }
    }
}