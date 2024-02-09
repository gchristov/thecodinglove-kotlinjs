package com.gchristov.thecodinglove.common.monitoring.domain

import com.gchristov.thecodinglove.common.kotlin.parseMainArgs

data class MonitoringEnvironment(
    val apiUrl: String,
) {
    companion object {
        fun of(args: Array<String>) = with(parseMainArgs(args)) {
            MonitoringEnvironment(
                apiUrl = requireNotNull(this["-apiUrl"]) { "API url not specified." }.first(),
            )
        }
    }
}