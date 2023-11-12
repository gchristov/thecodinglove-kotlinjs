package com.gchristov.thecodinglove.kmpcommonkotlin

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
sealed class JsonSerializer {
    abstract val json: Json

    object Default : JsonSerializer() {
        override val json: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = false
        }
    }

    object ExplicitNulls : JsonSerializer() {
        override val json: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            explicitNulls = true
        }
    }
}