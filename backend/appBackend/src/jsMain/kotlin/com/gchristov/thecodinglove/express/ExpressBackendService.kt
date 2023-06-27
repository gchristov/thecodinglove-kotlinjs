package com.gchristov.thecodinglove.express

import com.gchristov.thecodinglove.commonservice.BackendService
import com.gchristov.thecodinglove.commonservice.Request
import com.gchristov.thecodinglove.commonservice.Response
import com.gchristov.thecodinglove.kmpcommonkotlin.__dirname
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule

/*
 * Full implementation: https://github.com/wadejensen/kotlin-nodejs-example
 */
internal class ExpressBackendService : BackendService {
    private val express: dynamic = requireModule("express")
    private val app: dynamic = express()

    override fun startServer(port: Int) {
        println("Starting server: port=$port")
        app.listen(port)
        println("Server started")
    }

    override fun get(
        path: String,
        callback: (request: Request, response: Response) -> Unit,
    ) {
        app.get(path) { req, res ->
            callback.invoke(ExpressRequest(req), ExpressResponse(res))
        }
    }

    override fun serveStaticContent(localPath: String) {
        val path = requireModule("path")
        app.use(express.static(path.join(__dirname, localPath) as String))
    }
}