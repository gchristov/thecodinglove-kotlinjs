package com.gchristov.thecodinglove.commonservice

interface ParameterMap {
    operator fun <T> get(key: String): T?
}