package com.gchristov.thecodinglove.common.adapter.http

import com.gchristov.thecodinglove.common.kotlin.__dirname
import com.gchristov.thecodinglove.common.kotlin.requireModule

internal class ExpressHttpResponse(private val res: dynamic) : HttpResponse {
    override fun send(string: String) {
        res.send(string)
    }

    override fun sendFile(localPath: String) {
        val path = requireModule("path")
        res.sendFile(path.join(__dirname, localPath) as String)
    }

    override fun setHeader(
        header: String,
        value: String,
    ) {
        res.setHeader(header, value)
    }

    override fun redirect(path: String) {
        res.redirect(path)
    }

    override fun status(status: Int) {
        res.status(status)
    }
}