package com.gchristov.thecodinglove.commonservicedata

interface ParameterMap {
    operator fun <T> get(key: String): T?
}