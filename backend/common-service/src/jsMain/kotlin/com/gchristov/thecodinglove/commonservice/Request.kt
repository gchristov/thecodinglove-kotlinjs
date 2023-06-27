package com.gchristov.thecodinglove.commonservice

interface Request {
    val baseURL: String
    val hostname: String
    val ip: String
    val ips: Array<String>?
    val method: String
    val path: String
    val protocol: String
}