package com.gchristov.thecodinglove.commonservicedata

import com.gchristov.thecodinglove.commonservicedata.api.FirebaseFunctionsHttps

@JsModule("firebase-functions")
@JsNonModule
@JsName("FirebaseFunctions")
internal external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
}