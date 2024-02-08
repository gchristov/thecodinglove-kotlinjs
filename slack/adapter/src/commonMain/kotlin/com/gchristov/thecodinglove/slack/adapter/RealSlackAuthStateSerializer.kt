package com.gchristov.thecodinglove.slack.adapter

import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer
import io.ktor.util.*
import kotlinx.serialization.encodeToString

internal class RealSlackAuthStateSerializer(private val jsonSerializer: JsonSerializer) : SlackAuthStateSerializer {
    override fun serialize(authState: SlackAuthState): String {
        return jsonSerializer.json.encodeToString(authState.toAuthState()).encodeBase64()
    }
}