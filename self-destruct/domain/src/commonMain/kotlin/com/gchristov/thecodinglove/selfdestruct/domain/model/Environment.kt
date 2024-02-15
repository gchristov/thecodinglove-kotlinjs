package com.gchristov.thecodinglove.selfdestruct.domain.model

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class Environment(
    val port: Int,
    val apiUrl: String,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            Environment(
                port = requireNotNull(this["-port"]) { "-port not specified." }.first().toInt(),
                apiUrl = requireNotNull(this["-apiUrl"]) { "-apiUrl not specified." }.first(),
            )
        }
    }
}