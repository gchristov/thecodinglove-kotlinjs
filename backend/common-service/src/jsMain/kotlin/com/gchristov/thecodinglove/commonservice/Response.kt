package com.gchristov.thecodinglove.commonservice

interface Response {
    fun send(string: String)

    fun sendFile(localPath: String)
}