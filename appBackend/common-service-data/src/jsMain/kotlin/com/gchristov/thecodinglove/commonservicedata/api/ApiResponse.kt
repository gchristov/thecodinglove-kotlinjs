package com.gchristov.thecodinglove.commonservicedata.api

interface ApiResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int)
}

fun ApiResponse.sendJson(
    status: Int = 200,
    data: String,
) {
    status(status)
    setHeader(
        header = "Content-Type",
        value = "application/json"
    )
    send(data)
}