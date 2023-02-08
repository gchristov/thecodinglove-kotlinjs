package com.gchristov.thecodinglove.slacktestfixtures

import com.gchristov.thecodinglove.commonservicedata.api.ApiParameterMap
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest

@Suppress("UNCHECKED_CAST")
class FakeSlackApiRequest(
    fakeTimestamp: String? = null,
    fakeSignature: String? = null,
    fakeRawBody: String? = null
) : ApiRequest {
    override val headers: ApiParameterMap = object : ApiParameterMap {
        override operator fun <T> get(key: String): T? {
            return when (key) {
                "x-slack-request-timestamp" -> fakeTimestamp as? T
                "x-slack-signature" -> fakeSignature as? T
                else -> null
            }
        }
    }
    override val query: ApiParameterMap = object : ApiParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val body: Any? = null
    override val rawBody: String? = fakeRawBody
}