package com.gchristov.thecodinglove.common.kotlin

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
sealed class JsonSerializer {
    abstract val json: Json

    data object Default : JsonSerializer() {
        override val json: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    data object ExplicitNulls : JsonSerializer() {
        override val json: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = true
        }
    }
}