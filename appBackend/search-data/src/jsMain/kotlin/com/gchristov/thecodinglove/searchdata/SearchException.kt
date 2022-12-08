package com.gchristov.thecodinglove.searchdata

sealed class SearchException : Exception() {
    object Empty : SearchException()
    object Exhausted : SearchException()
    object SessionNotFound : SearchException()
}