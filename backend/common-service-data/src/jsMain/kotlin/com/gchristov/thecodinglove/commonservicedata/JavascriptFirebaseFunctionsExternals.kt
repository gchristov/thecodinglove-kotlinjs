package com.gchristov.thecodinglove.commonservicedata

import com.gchristov.thecodinglove.commonservicedata.api.FirebaseFunctionsHttps
import com.gchristov.thecodinglove.commonservicedata.pubsub.FirebaseFunctionsPubSub

external var exports: dynamic

@JsModule("firebase-functions")
@JsNonModule
@JsName("FirebaseFunctions")
internal external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
    val pubsub: FirebaseFunctionsPubSub
}