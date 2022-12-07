package com.gchristov.thecodinglove

fun main(args: Array<String>) {
    serveApi(args)
}

internal expect fun serveApi(args: Array<String>)