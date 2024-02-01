package com.gchristov.thecodinglove.common.adaptertestfixtures

import com.gchristov.thecodinglove.common.adapter.http.HttpResponse
import kotlin.test.assertEquals

class FakeHttpResponse : HttpResponse {
    private var lastHeader: String? = null
    private var lastHeaderValue: String? = null
    private var lastData: String? = null
    private var lastStatus: Int? = null
    private var lastRedirectPath: String? = null
    private var lastFilePath: String? = null

    override fun send(string: String) {
        lastData = string
    }

    override fun sendFile(localPath: String) {
        lastFilePath = localPath
    }

    override fun setHeader(header: String, value: String) {
        lastHeader = header
        lastHeaderValue = value
    }

    override fun redirect(path: String) {
        lastRedirectPath = path
    }

    override fun status(status: Int) {
        lastStatus = status
    }

    fun assertEquals(
        header: String?,
        headerValue: String?,
        data: String?,
        status: Int?,
        filePath: String?,
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
        assertEquals(
            expected = filePath,
            actual = lastFilePath
        )
    }
}