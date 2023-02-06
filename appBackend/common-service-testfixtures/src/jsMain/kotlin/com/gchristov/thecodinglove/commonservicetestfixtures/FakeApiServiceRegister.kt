package com.gchristov.thecodinglove.commonservicetestfixtures

import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import kotlin.test.assertEquals

class FakeApiServiceRegister : ApiServiceRegister {
    private var invocations = 0

    override fun register(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) {
        invocations++
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = invocations
        )
    }
}