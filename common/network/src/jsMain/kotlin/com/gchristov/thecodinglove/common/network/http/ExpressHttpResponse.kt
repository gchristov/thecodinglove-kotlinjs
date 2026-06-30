package com.gchristov.thecodinglove.common.network.http

import com.gchristov.thecodinglove.common.kotlin.__dirname
import com.gchristov.thecodinglove.common.kotlin.requireModule
import com.gchristov.thecodinglove.common.kotlin.safeJsCall

internal class ExpressHttpResponse(private val res: dynamic) : HttpResponse {
    override suspend fun send(string: String) = safeJsCall("Error sending HTTP response") {
        res.send(string)
        Unit
    }

    override suspend fun sendFile(localPath: String) = safeJsCall("Error sending file") {
        val path = requireModule("path")
        res.sendFile(path.join(__dirname, localPath) as String)
        Unit
    }

    override suspend fun setHeader(header: String, value: String) = safeJsCall("Error setting HTTP header") {
        res.setHeader(header, value)
        Unit
    }

    override suspend fun redirect(path: String) = safeJsCall("Error redirecting HTTP response") {
        res.redirect(path)
        Unit
    }

    override suspend fun status(status: Int) = safeJsCall("Error setting HTTP status") {
        res.status(status)
        Unit
    }
}
