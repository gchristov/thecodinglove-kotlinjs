package com.gchristov.thecodinglove.common.analyticstestfixtures

import com.gchristov.thecodinglove.common.analytics.Analytics

class FakeAnalytics : Analytics {
    override fun sendEvent(
        clientId: String,
        name: String,
        params: Map<String, String>?,
    ) {
        // No-op
    }
}