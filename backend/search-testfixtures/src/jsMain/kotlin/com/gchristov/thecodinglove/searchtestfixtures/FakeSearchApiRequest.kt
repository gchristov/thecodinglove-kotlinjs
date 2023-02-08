package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.commonservicedata.api.ApiParameterMap
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest

@Suppress("UNCHECKED_CAST")
class FakeSearchApiRequest(
    fakeSearchSessionId: String? = null,
    fakeSearchQuery: String? = null,
) : ApiRequest {
    override val headers: ApiParameterMap = object : ApiParameterMap {
        override operator fun <T> get(key: String): T? = null
    }
    override val query: ApiParameterMap = object : ApiParameterMap {
        override fun <T> get(key: String): T? {
            return when (key) {
                "searchQuery" -> fakeSearchQuery as? T
                "searchSessionId" -> fakeSearchSessionId as? T
                else -> null
            }
        }
    }
    override val body: Any? = null
    override val rawBody: String? = null
}