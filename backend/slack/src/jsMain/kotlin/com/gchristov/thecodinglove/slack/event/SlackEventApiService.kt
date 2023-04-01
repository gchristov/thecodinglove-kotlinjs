package com.gchristov.thecodinglove.slack.event

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.exports
import kotlinx.serialization.json.Json

class SlackEventApiService(
    apiServiceRegister: ApiServiceRegister,
    jsonSerializer: Json,
    log: Logger,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun register() {
        exports.slackEventApi = registerForApiCallbacks()
    }
}