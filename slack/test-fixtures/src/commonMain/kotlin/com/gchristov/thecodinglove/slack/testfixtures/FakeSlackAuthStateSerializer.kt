package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer

class FakeSlackAuthStateSerializer(
    private val serializedState: String = "serialized_state",
) : SlackAuthStateSerializer {
    override fun serialize(authState: SlackAuthState): String = serializedState
}
