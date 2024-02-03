package com.gchristov.thecodinglove.slack.adapter

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.ports.SlackAuthStateSerializer
import io.ktor.util.*
import kotlinx.serialization.encodeToString

class RealSlackAuthStateSerializer(private val jsonSerializer: JsonSerializer) : SlackAuthStateSerializer {
    override fun serialize(authState: SlackAuthState): String {
        return jsonSerializer.json.encodeToString(ApiSlackAuthState.of(authState)).encodeBase64()
    }
}