package com.gchristov.thecodinglove.common.pubsub

import kotlin.js.Promise

@JsModule("@google-cloud/tasks")
@JsNonModule
internal external object GoogleCloudTasksExternals {
    // Named CloudTasks (not CloudTasksClient) for consistency with GoogleCloudPubSubExternals.PubSub.
    // @JsName keeps the binding pointed at the real exported class name.
    @JsName("CloudTasksClient")
    class CloudTasks {
        val auth: GoogleAuth
        fun queuePath(project: String, location: String, queue: String): String
        fun createTask(request: dynamic): Promise<dynamic>
    }

    class GoogleAuth {
        fun getProjectId(): Promise<String>
        fun getCredentials(): Promise<dynamic>
    }
}
