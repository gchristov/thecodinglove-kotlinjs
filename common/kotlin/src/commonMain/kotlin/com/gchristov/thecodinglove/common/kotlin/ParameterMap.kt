package com.gchristov.thecodinglove.common.kotlin

interface ParameterMap {
    operator fun <T> get(key: String): T?
}