package com.gchristov.thecodinglove.slack.domain.port

import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState

interface SlackAuthStateSerializer {
    fun serialize(authState: SlackAuthState): String
}