package com.gchristov.thecodinglove.kmpcommonkotlin

data class AppConfig(
    val logLevel: String,
    val networkHtmlLogLevel: String,
    val networkJsonLogLevel: String,
    val publicUrl: String,
)