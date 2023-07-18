package com.gchristov.thecodinglove.commonservice.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.__dirname
import com.gchristov.thecodinglove.kmpcommonkotlin.requireModule
import io.ktor.http.*

internal class ExpressHttpService(
    private val log: Logger,
) : HttpService {
    private val express: dynamic = requireModule("express")
    private val app: dynamic = express()
    private var port: Int? = null

    override suspend fun initialise(
        handlers: List<HttpHandler>,
        staticWebsiteRoot: String?,
        port: Int
    ): Either<Throwable, Unit> = try {
        log.d("Initialising ${this::class.simpleName}")
        this.port = port

        staticWebsiteRoot?.let {
            val path = requireModule("path")
            app.use(express.static(path.join(__dirname, it) as String))
        }

        handlers
            .map { handler ->
                handler
                    .initialise()
                    .map {
                        log.d("Attaching ${handler::class.simpleName}")
                        handler.attach(app)
                    }
            }
            .sequence()
            .flatMap {
                log.d("${this::class.simpleName} initialised")
                Either.Right(Unit)
            }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error initialising ${this::class.simpleName}${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun start(): Either<Throwable, Unit> = try {
        app.listen(requireNotNull(port))
        log.d("${this::class.simpleName} started on port $port")
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error starting ${this::class.simpleName}${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

private fun HttpHandler.attach(app: dynamic) {
    val handlerConfig = httpConfig()
    val contentConfig = handlerConfig.contentType.toExpressContentConfig()
    when (handlerConfig.method) {
        HttpMethod.Get -> app.get(handlerConfig.path, contentConfig) { req, res ->
            handle(ExpressHttpRequest(req), ExpressHttpResponse(res))
        }

        HttpMethod.Post -> app.post(handlerConfig.path, contentConfig) { req, res ->
            handle(ExpressHttpRequest(req), ExpressHttpResponse(res))
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