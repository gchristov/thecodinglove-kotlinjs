package com.gchristov.thecodinglove.commonservice

interface BackendService {
    fun startServer(port: Int)

    fun get(
        path: String,
        callback: (request: Request, response: Response) -> Unit,
    )

    fun serveStaticContent(localPath: String)
}