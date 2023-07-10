package com.gchristov.thecodinglove.express

import com.gchristov.thecodinglove.commonservice.HttpBackendService
import com.gchristov.thecodinglove.commonservice.HttpHandler
import com.gchristov.thecodinglove.kmpcommonkotlin.__dirname
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule
import io.ktor.http.*

/*
 * Full implementation: https://github.com/wadejensen/kotlin-nodejs-example
 */
internal class ExpressHttpBackendService : HttpBackendService {
    private val express: dynamic = requireModule("express")
    private val app: dynamic = express()

    override fun start(port: Int) {
        app.listen(port)
    }

    override fun serveStaticContent(localPath: String) {
        val path = requireModule("path")
        app.use(express.static(path.join(__dirname, localPath) as String))
    }

    override fun registerGetHandler(
        path: String,
        contentType: ContentType,
        handler: HttpHandler,
    ) {
        app.get(path, contentType.toExpressContentConfig()) { req, res ->
            handler.handle(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }
    }
}

private fun ContentType.toExpressContentConfig(): dynamic {
    val parser = requireModule("body-parser")
    return when (this) {
        ContentType.Application.FormUrlEncoded -> parser.urlencoded(js(JsUrlEncodedConfig))
        else -> parser.json(js(JsJsonConfig))
    }
}

private const val JsJsonConfig = """
{
    verify: function (req, res, buf, encoding) {
        req.bodyString = buf.toString();
    }
}
"""
private const val JsUrlEncodedConfig = """
{
    extended: false,
    verify: function (req, res, buf, encoding) {
        req.bodyString = buf.toString();
    }
}
"""