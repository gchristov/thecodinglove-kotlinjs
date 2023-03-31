package com.gchristov.thecodinglove.commonservicetestfixtures

import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import kotlin.test.assertEquals

class FakeApiResponse : ApiResponse {
    private var lastHeader: String? = null
    private var lastHeaderValue: String? = null
    private var lastData: String? = null
    private var lastStatus: Int? = null
    private var lastRedirectPath: String? = null

    override fun setHeader(
        header: String,
        value: String
    ) {
        lastHeader = header
        lastHeaderValue = value
    }

    override fun send(data: String) {
        lastData = data
    }

    override fun status(status: Int) {
        lastStatus = status
    }

    override fun redirect(path: String) {
        lastRedirectPath = path
    }

    fun assertEquals(
        header: String?,
        headerValue: String?,
        data: String?,
        status: Int?,
    ) {
        assertEquals(
            expected = header,
            actual = lastHeader
        )
        assertEquals(
            expected = headerValue,
            actual = lastHeaderValue
        )
        assertEquals(
            expected = data,
            actual = lastData
        )
        assertEquals(
            expected = status,
            actual = lastStatus
        )
    }
}