package com.gchristov.thecodinglove.express

import com.gchristov.thecodinglove.commonservice.Response
import com.gchristov.thecodinglove.kmpcommonkotlin.__dirname
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule

/*
 * Full implementation: https://github.com/wadejensen/kotlin-nodejs-example
 */
internal class ExpressResponse(private val res: dynamic) : Response {
    override fun send(string: String) {
        res.send(string)
    }

    override fun sendFile(localPath: String) {
        val path = requireModule("path")
        res.sendFile(path.join(__dirname, localPath) as String)
    }
}