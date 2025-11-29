package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.__dirname
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.requireModule
import io.ktor.http.*

internal class ExpressHttpService(
    private val log: Logger,
) : HttpService {
    private val tag = this::class.simpleName
    private val express: dynamic = requireModule("express")
    private val app: dynamic = express()
    private var port: Int? = null

    override suspend fun initialise(
        handlers: List<HttpHandler>,
        staticWebsiteRoot: String?,
        port: Int
    ): Either<Throwable, Unit> = try {
        log.debug(tag, "Initialising")
        this.port = port

        staticWebsiteRoot?.let {
            val path = requireModule("path")
            app.use(express.static(path.join(__dirname, it) as String))
        }

        either {
            handlers.forEach { handler ->
                handler.initialise().bind()
                log.debug(handler::class.simpleName, "Attaching")
                handler.attach(app)
            }
            log.debug(tag, "Initialised")
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error initialising $tag${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun start(): Either<Throwable, Unit> = try {
        app.listen(requireNotNull(port))
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error starting $tag${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

private fun HttpHandler.attach(app: dynamic) {
    val handlerConfig = httpConfig()
    val contentConfig = handlerConfig.contentType.toExpressContentConfig()
    when (handlerConfig.method) {
        HttpMethod.Get -> app.get(handlerConfig.path, contentConfig) { req, res ->
            handleHttpRequest(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }

        HttpMethod.Post -> app.post(handlerConfig.path, contentConfig) { req, res ->
            handleHttpRequest(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }

        HttpMethod.Put -> app.put(handlerConfig.path, contentConfig) { req, res ->
            handleHttpRequest(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }

        HttpMethod.Delete -> app.delete(handlerConfig.path, contentConfig) { req, res ->
            handleHttpRequest(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }

        else -> throw IllegalStateException("Unsupported HTTP method used: method=${handlerConfig.method}")
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