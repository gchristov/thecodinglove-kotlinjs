package com.gchristov.thecodinglove.searchdata

sealed class SearchError : Throwable() {
    object Empty : SearchError()
    object Exhausted : SearchError()
    object SessionNotFound : SearchError()
}