package com.gchristov.thecodinglove.common.adapter

interface ParameterMap {
    operator fun <T> get(key: String): T?
}