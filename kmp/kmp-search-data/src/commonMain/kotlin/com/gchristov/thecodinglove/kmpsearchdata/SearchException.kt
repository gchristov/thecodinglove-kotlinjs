package com.gchristov.thecodinglove.kmpsearchdata

sealed class SearchException : Exception() {
    object Empty : SearchException()
    object Exhausted : SearchException()
}