package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservice.HttpRequest
import com.gchristov.thecodinglove.commonservice.HttpResponse
import com.gchristov.thecodinglove.commonservice.decodeBodyFromJson
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.json

class SearchHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
) : BaseHttpHandler(
    jsonSerializer = jsonSerializer,
    log = log
) {

    override suspend fun handleAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        println("QUERY=${request.query.get<String>("test")}")
        println("HEADER=${request.headers.get<String>("test")}")
        println("BODY_STRING=${request.bodyString}")

        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-pubsub.json"
        val topic = GoogleCloudPubSub2.PubSub().topic("test-1")
        if (!topic.exists().await().first()) {
            println("Creating new topic")
            topic.create().await()
            println("Created new topic")
        }
        val subscription = topic.subscription("test-sub-1")
        if (!subscription.exists().await().first()) {
            println("Creating new subscription")
            val options = json(
                "pushEndpoint" to "https://codinglove.serveo.net/pubsub/notifications"
            )
            subscription.create(options).await()
            println("Created new subscription")
        }
        val messageId = topic
            .publish(Buffer.from("Hello!"))
            .await()
        log.d("PubSub message sent: id=$messageId")

        return request.decodeBodyFromJson<BodyTest>(jsonSerializer, log).flatMap {
            println("DECODED_BODY=$it")
            super.handleAsync(request, response)
        }
    }
}

@Serializable
data class BodyTest(
    val title: String?,
    val subtitle: String?,
)