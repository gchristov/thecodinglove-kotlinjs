package com.gchristov.thecodinglove.commonservicedata.pubsub

import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer

class RealPubSubSender(private val projectId: String) : PubSubSender {
    override fun sendMessage(
        topic: String,
        body: String
    ) {
        println(
            "Sending PubSub message\n" +
                    "topic: $topic\n" +
                    "body: $body"
        )
        GoogleCloudPubSub(projectId).topic(topic).publish(Buffer.from(body))
    }
}